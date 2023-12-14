

package com.wallet.ctc.ui.me.about;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.app.AppApplication;


public class LocalImageHelper {
    private static LocalImageHelper instance;
    private final Context context;
    final List<LocalFile> checkedItems = new ArrayList<>();


    public String getCameraImgPath() {
        return CameraImgPath;
    }

    public String setCameraImgPath() {
        String foloder = new AppApplication().getCachePath()
                + "/PostPicture/";
        File savedir = new File(foloder);
        if (!savedir.exists()) {
            savedir.mkdirs();
        }
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss")
                .format(new Date());
        
        String picName = timeStamp + ".jpg";
        
        CameraImgPath = foloder + picName;
        return CameraImgPath;
    }

    
    private String CameraImgPath;
    
    private static final String[] STORE_IMAGES = {
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.ORIENTATION,
            MediaStore.Images.Media.SIZE,
    };
    
    private static final String[] THUMBNAIL_STORE_IMAGE = {
            MediaStore.Images.Thumbnails._ID,
            MediaStore.Images.Thumbnails.DATA
    };

    final List<LocalFile> paths = new ArrayList<>();

    final Map<String, List<LocalFile>> folders = new HashMap<>();

    private LocalImageHelper(Context context) {
        this.context = context;
    }

    public Map<String, List<LocalFile>> getFolderMap() {
        return folders;
    }

    public static LocalImageHelper getInstance() {
        return instance;
    }
    public static LocalImageHelper getInstance(Context context) {
        if(instance==null){
            instance = new LocalImageHelper(context);
        }
        return instance;
    }

    public static void init(Context context) {
        if(null==instance){
            instance = new LocalImageHelper(context);
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                instance.initImage();
            }
        }).start();
    }

    public boolean isInited() {
        return paths.size() > 0;
    }

    public List<LocalFile> getCheckedItems() {
        return checkedItems;
    }

    private boolean resultOk;

    public boolean isResultOk() {
        return resultOk;
    }

    public void setResultOk(boolean ok) {
        resultOk = ok;
    }

    private boolean isRunning = false;

    public synchronized void initImage() {
        if (isRunning) {
            return;
        }
        isRunning = true;
        if (isInited()) {
            return;
        }
        
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,  
                STORE_IMAGES,   
                null,         
                null,         
                MediaStore.Images.Media.DATE_TAKEN + " DESC"); 
        if (cursor == null) {
            return;
        }
        int photoSizeIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String path = cursor.getString(1);
            File file = new File(path);
            
            if (file.exists()) {
                
                String thumbUri = getThumbnail(id, path);
                
                String uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI.buildUpon().
                        appendPath(Integer.toString(id)).build().toString();
                if (StringUtils.isEmpty(uri)) {
                    continue;
                }
                if (StringUtils.isEmpty(thumbUri)) {
                    thumbUri = uri;
                }
                
                String folder = file.getParentFile().getName();

                LocalFile localFile = new LocalFile();
                
                localFile.setOriginalUri(path);
                localFile.setThumbnailUri(thumbUri);
                String size = cursor.getString(photoSizeIndex);
                localFile.setSize(size);
                int degree = cursor.getInt(2);
                if (degree != 0) {
                    degree = degree + 180;
                }
                localFile.setOrientation(360 - degree);

                paths.add(localFile);
                
                if (folders.containsKey(folder)) {
                    folders.get(folder).add(localFile);
                } else {
                    List<LocalFile> files = new ArrayList<>();
                    files.add(localFile);
                    folders.put(folder, files);
                }
            }
        }
        folders.put("", paths);
        cursor.close();
        isRunning = false;
    }

    private String getThumbnail(int id, String path) {
        
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
                THUMBNAIL_STORE_IMAGE,
                MediaStore.Images.Thumbnails.IMAGE_ID + " = ?",
                new String[]{id + ""},
                null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            int thumId = cursor.getInt(0);
            String uri = MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI.buildUpon().
                    appendPath(Integer.toString(thumId)).build().toString();
            cursor.close();
            return uri;
        }
        cursor.close();
        return null;
    }

    public List<LocalFile> getFolder(String folder) {
        return folders.get(folder);
    }

    public void clear() {
        checkedItems.clear();
        String foloder = new AppApplication().getCachePath()
                + "/PostPicture/";
        File savedir = new File(foloder);
        if (savedir.exists()) {
            deleteFile(savedir);
        }
    }

    public void deleteFile(File file) {

        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    deleteFile(files[i]);
                }
            }
        } else {
        }
    }

    public static Bitmap revitionImageSize(String path) throws IOException {
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(
                new File(path)));
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(in, null, options);
        in.close();
        int i = 0;
        Bitmap bitmap = null;
        while (true) {
            if ((options.outWidth >> i <= 1000)
                    && (options.outHeight >> i <= 1000)) {
                in = new BufferedInputStream(
                        new FileInputStream(new File(path)));
                options.inSampleSize = (int) Math.pow(2.0D, i);
                options.inJustDecodeBounds = false;
                bitmap = BitmapFactory.decodeStream(in, null, options);
                break;
            }
            i += 1;
        }
        return bitmap;
    }
}
