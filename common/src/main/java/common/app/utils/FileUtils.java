

package common.app.utils;

import static android.os.Environment.DIRECTORY_PICTURES;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;

import common.app.AppApplication;
import common.app.BuildConfig;

public class FileUtils {

    public static String SDPATH = AppApplication.getContext().getExternalFilesDir(null) + "/" + BuildConfig.SCHEME + "/";

    
    public static void CreateFile() {
        File dirFirstFolder = new File(FileUtils.SDPATH);
        if (!dirFirstFolder.exists()) { 
            dirFirstFolder.mkdirs();

        }
    }

    
    public static void saveBitmap(Bitmap bm, String picName) {
        try {
            if (!isFileExist("")) {
                File tempf = createSDDir("");
            }
            File f = new File(SDPATH, picName + ".jpg");
            if (f.exists()) {
                f.delete();
            }
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
    public static String saveBitmap(Bitmap bm) {
        String savePath = null;
        try {
            if (!isFileExist("")) {
                File tempf = createSDDir("");
            }
            String fileName = String.valueOf(System.currentTimeMillis());
            File f = new File(SDPATH, fileName + ".jpg");
            if (f.exists()) {
                f.delete();
            }
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            savePath = f.getAbsolutePath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return savePath;
    }

    
    public static File createSDDir(String dirName) throws IOException {
        File dir = new File(SDPATH + dirName);
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {

            System.out.println("createSDDir:" + dir.getAbsolutePath());
            System.out.println("createSDDir:" + dir.mkdir());
        }
        return dir;
    }


    public static boolean isFileExist(String fileName) {
        File file = new File(SDPATH + fileName);
        file.isFile();
        return file.exists();
    }


    
    public static void deleteDir() {
        File dir = new File(SDPATH);
        if (dir == null || !dir.exists() || !dir.isDirectory()) {
            return;
        }

        for (File file : dir.listFiles()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                deleteDir();
            }
        }
        dir.delete();
    }


    
    public static int deleteAllFiles(File dir) {
        int deletedFiles = 0;
        if (dir == null || !dir.exists() || !dir.isDirectory())
            return deletedFiles;
        for (File file : dir.listFiles()) {
            if (file.isFile()) {
                deletedFiles++;
                boolean b = file.delete(); 
                if (!b) {
                    file.deleteOnExit();
                    deletedFiles--;
                    deleteAllFiles(file);
                }
            } else if (file.isDirectory()) {
                deletedFiles += deleteAllFiles(file); 
            }
        }
        dir.delete();
        return deletedFiles;
    }

    
    public static int delWebview(File root) {
        int deletedFiles = 0;
        File[] files = root.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) { 
                    if (f.toString().endsWith("webview")) {
                        deletedFiles += deleteAllFiles(f);
                    }
                }
            }
        }
        return deletedFiles;
    }






    public static  String md5(File file) {
        if (!file.isFile()) {
            return "";
        }
        try {
            byte[] buffer = new byte[1024];

            MessageDigest digest = MessageDigest.getInstance("MD5");
            FileInputStream inputStream = new FileInputStream(file);

            do {
                int len = inputStream.read(buffer);
                if (len == -1) {
                    break;
                }
                digest.update(buffer, 0, len);
            } while (true);

            return String.format("%032x", new BigInteger(1, digest.digest()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    
    public static void saveWebImage(Context context, String imgData, FileSaveResult callback) {
        if (imgData.startsWith("data:image")) {
            
            int start = imgData.indexOf(",");
            String data = imgData.substring(start + 1);
            saveBase64Img(data, callback);

        } else if (imgData.startsWith("http")) {
            GlideUtil.showImg(context, imgData, null, img -> {
                if (null != img) {
                    saveBitmap(img, callback);
                }
            });
        }
    }

    public interface FileSaveResult {
        public void onSave(boolean success, String filePath, String fileName);
    }


    
    public static void saveBase64Img(String base64ImgData, FileSaveResult result) {
        if (TextUtils.isEmpty(base64ImgData)) {
            result.onSave(false, null, null);
            return;
        }
        try {
            byte[] b = Base64Utils.decode(base64ImgData);
            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {
                    b[i] += 256;
                }
            }
            String savePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
            String fileName = System.currentTimeMillis() + "";
            File f = new File(savePath, fileName + ".jpg");
            if (f.exists()) {
                f.delete();
            }
            OutputStream out = new FileOutputStream(f);
            out.write(b);
            out.flush();
            out.close();
            String filePath = f.getAbsolutePath();
            result.onSave(true, filePath, fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            result.onSave(false, null, null);
        } catch (IOException e) {
            e.printStackTrace();
            result.onSave(false, null, null);
        }
    }

    
    public static void saveBitmap(Bitmap bitmap, FileSaveResult result) {
        if (null == bitmap) {
            result.onSave(false, null, null);
            return;
        }
        String savePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
        String fileName = System.currentTimeMillis() + "";
        try {
            File f = new File(savePath, fileName + ".jpg");
            if (f.exists()) {
                f.delete();
            }
            FileOutputStream out = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            String filePath = f.getAbsolutePath();
            result.onSave(true, filePath, fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            result.onSave(false, null, null);
        } catch (IOException e) {
            e.printStackTrace();
            result.onSave(false, null, null);
        }
    }





    
    public static String getFileNameOrSuffix(String filePath, boolean onLySuffix, boolean onlyFileName) {
        if (TextUtils.isEmpty(filePath)) {
            return "";
        }
        int idx = filePath.lastIndexOf("/");
        String fileName = "";
        String suffix = "";
        if (idx != -1) {
            fileName = filePath.substring(idx + 1);
        } else {
            fileName = filePath;
        }
        if (!TextUtils.isEmpty(fileName)) {
            idx = fileName.lastIndexOf(".");
            if (idx != -1) {
                suffix = fileName.substring(idx);
                fileName = fileName.substring(0, idx);
            }
        }
        if (onLySuffix) {
            return suffix;
        } else if (onlyFileName) {
            return fileName;
        } else {
            return fileName + suffix;
        }
    }

    public static Uri createImageFile(Context context) {
        try {
            Uri uri;
            String fileName = System.currentTimeMillis() + ".png";
            File rootFile = new File(SDPATH);
            if (!rootFile.exists()) {
                rootFile.mkdirs();
            }
            File imgFile;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                imgFile = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES) + "/" + fileName);
                
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, imgFile.getAbsolutePath());
                values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
                values.put(MediaStore.Images.Media.MIME_TYPE, "image
    public static String getRealFilePath(final Context context, final Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    
    public static String getAssetsFileConent(Context context, String fileName) {
        if (null == context || TextUtils.isEmpty(fileName)) {
            return "";
        }
        try {
            InputStream is = context.getAssets().open(fileName);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
            String content = "";
            String bufferStr = "";
            while ((bufferStr = bufferedReader.readLine()) != null) {
                content += bufferStr;
            }
            return content;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    
    public static String copyAssetGetFilePath(String assetsPath) {
        Context context = AppApplication.getContext();
        if (null == context || TextUtils.isEmpty(assetsPath)) {
            return "";
        }
        try {
            File filesDir = AppApplication.getContext().getFilesDir();
            if (!filesDir.exists()) {
                filesDir.mkdirs();
            }
            File outFile = new File(filesDir, assetsPath);
            if (outFile.exists() && outFile.isFile() && outFile.length() > 0) {
                return outFile.getPath();
            }
            String outFilename = outFile.getAbsolutePath();
            File folderFile = outFile.getParentFile();
            if (!folderFile.exists()) {
                folderFile.mkdirs();
            }
            if (!outFile.exists()) {
                boolean res = outFile.createNewFile();
                if (!res) {
                    Log.e("FileUtils", "outFile not exist!(" + outFilename + ")");
                    return "";
                }
            }
            InputStream is = context.getAssets().open(assetsPath);

            FileOutputStream fos = new FileOutputStream(outFile);
            byte[] buffer = new byte[1024];
            int byteCount;
            while ((byteCount = is.read(buffer)) != -1) {
                fos.write(buffer, 0, byteCount);
            }
            fos.flush();
            is.close();
            fos.close();
            return outFile.getPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

}
