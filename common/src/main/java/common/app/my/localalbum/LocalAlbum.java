

package common.app.my.localalbum;

import android.Manifest;
import android.content.Intent;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import common.app.R;
import common.app.base.base.PermissionListener;
import common.app.mall.BaseActivity;
import common.app.my.localalbum.adapter.LocalAlbumAdapter;
import common.app.my.localalbum.bean.LocalFile;
import common.app.my.localalbum.utils.ExtraKey;
import common.app.my.localalbum.utils.ImageUtils;
import common.app.my.localalbum.utils.LocalImageHelper;
import common.app.my.localalbum.utils.StringUtils;
import common.app.ui.view.MyProgressDialog;
import common.app.ui.view.TitleBarView;



public class LocalAlbum extends BaseActivity {
    private TitleBarView titleBarView;
    private MyProgressDialog mydialog;
    private ListView listView;
    private LocalImageHelper helper;
    private List<String> folderNames;
    private Intent intent;
    private int num = 0;
    
    protected boolean isDestroy = false;
    private String[] SD_PERMISSION = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setView(R.layout.localalbum_activity);
    }

    @Override
    protected void initView() {
        super.initView();
        requestRuntimePermisssions(SD_PERMISSION, new PermissionListener() {
            @Override
            public void onGranted() {
                LocalImageHelper.init(LocalAlbum.this);
            }

            @Override
            public void onDenied(List<String> deniedList) {
                finish();
            }
        });
        titleBarView = (TitleBarView) findViewById(R.id.title_bar);
        listView = (ListView) findViewById(R.id.local_album_list);
       findViewById(R.id.itemLinearLayout).setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if (LocalImageHelper.getInstance().getCheckedItems().size() + LocalImageHelper.getInstance().getCheckedItems().size() >= num) {
                   Toast.makeText(LocalAlbum.this, ""+num+"", Toast.LENGTH_SHORT).show();
                   return;
               }
               Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
               
               String cameraPath = LocalImageHelper.getInstance().setCameraImgPath();
               File file = new File(cameraPath);
               intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
               startActivityForResult(intent,
                       ImageUtils.REQUEST_CODE_GETIMAGE_BYCAMERA);
           }
       });
        
    }

    @Override
    protected void initData() {
        super.initData();
        intent = getIntent();
        num = intent.getIntExtra("num",9);
        if(0==num){
            toast("！");
            finish();
            return;
        }
        titleBarView.setOnTitleBarClickListener(new TitleBarView.TitleBarClickListener() {
            @Override
            public void leftClick() {
                helper.getCheckedItems().clear();
                finish();
            }

            @Override
            public void rightClick() {
                intent = new Intent();
                LocalAlbum.this.setResult(RESULT_OK,intent);
                LocalAlbum.this.finish();
            }
        });
        titleBarView.setRightTextVisable(false);
        helper = LocalImageHelper.getInstance();
        mydialog = new MyProgressDialog(this, getResources().getString(R.string.hold_on));
        mydialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                
                LocalImageHelper.getInstance().initImage();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        
                        if (!isDestroy) {
                            initAdapter();
                            mydialog.dismiss();
                            titleBarView.setRightTextVisable(true);
                        }
                    }
                });
            }
        }).start();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                intent = new Intent(LocalAlbum.this, LocalAlbumDetail.class);
                intent.putExtra(ExtraKey.LOCAL_FOLDER_NAME, folderNames.get(i));
                intent.putExtra("num",num);
                intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
         if(LocalImageHelper.getInstance().isResultOk()){
            if (LocalImageHelper.getInstance().getCheckedItems().size() > 0) {
                titleBarView.setRightText("(" + (LocalImageHelper.getInstance().getCheckedItems().size()) + "/"+num+")");
                titleBarView.setRightTextOnclick(true);
            } else {
                titleBarView.setRightText("");
                titleBarView.setRightTextOnclick(false);

            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isDestroy = true;
    }

    public void initAdapter() {
        folderNames = new ArrayList<>();
        Iterator iter = helper.getFolderMap().entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String key = (String) entry.getKey();
            folderNames.add(key);
        }
        
        Collections.sort(folderNames, new Comparator<String>() {
            @Override
            public int compare(String arg0, String arg1) {
                Integer num1 = helper.getFolder(arg0).size();
                Integer num2 = helper.getFolder(arg1).size();
                return num2.compareTo(num1);
            }
        });
        listView.setAdapter(new LocalAlbumAdapter(this, helper.getFolderMap(), folderNames));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case ImageUtils.REQUEST_CODE_GETIMAGE_BYCAMERA:
                    String cameraPath = LocalImageHelper.getInstance().getCameraImgPath();
                    if (StringUtils.isEmpty(cameraPath)) {
                        Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    File file = new File(cameraPath);
                    if (file.exists()) {
                        Uri uri = Uri.fromFile(file);
                        LocalFile localFile = new LocalFile();
                        localFile.setThumbnailUri(uri.toString());
                        localFile.setOriginalUri(uri.toString());
                        localFile.setOrientation(getBitmapDegree(cameraPath));
                        LocalImageHelper.getInstance().getCheckedItems().add(localFile);
                        LocalImageHelper.getInstance().setResultOk(true);
                        
                        
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        }, 1000);
                    } else {
                        toast("");
                    }
                    break;
            }
        }
    }

    
    private int getBitmapDegree(String path) {
        int degree = 0;
        try {
            
            ExifInterface exifInterface = new ExifInterface(path);
            
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

}
