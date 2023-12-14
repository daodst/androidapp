

package com.app.my;


import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;

import com.app.R;
import com.app.my.zoom.PhotoView;
import com.app.my.zoom.PhotoViewAttacher;
import com.app.my.zoom.ViewPagerFixed;
import com.app.pojo.LocationBean;
import com.app.view.SmoothImageView;

import java.util.ArrayList;
import java.util.List;

import common.app.base.base.PermissionListener;
import common.app.mall.BaseActivity;
import common.app.ui.view.MyProgressDialog;
import common.app.utils.AllUtils;
import common.app.utils.FileUtils;
import common.app.utils.GlideUtil;



public class Photo extends BaseActivity{
    private ViewPagerFixed pager;
    private MyPageAdapter adapter;
    private ImageView[] imageViews;
    private List<String> mDatas = new ArrayList<>();
    private List<LocationBean> mLocationlist = new ArrayList<>();
    private int mPosition;
    private int mLocationX;
    private int mLocationY;
    private int mWidth;
    private int mHeight;
    private ArrayList<View> advPics;
    private Intent intent;
    private SmoothImageView imageView ;
    private RelativeLayout all;
    private LinearLayout group;
    private int size;
    private String[] SD_PERMISSION = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setView(R.layout.activity_photo);
    }

    @Override
    protected void initView() {
        super.initView();
        pager = (ViewPagerFixed) findViewById(R.id.gallery01);
        imageView = (SmoothImageView) findViewById(R.id.img);
        all = (RelativeLayout) findViewById(R.id.all);
        group = (LinearLayout) findViewById(R.id.group);
    }

    @Override
    protected void initData() {
        super.initData();
        intent = getIntent();
        mPosition = intent.getIntExtra("position", 0);
        mDatas = (ArrayList<String>) intent.getSerializableExtra("images");
        size = mDatas.size();
        mLocationlist = (ArrayList<LocationBean>) intent.getSerializableExtra("locationlist");
        if(null!=mLocationlist){
            LocationBean item = mLocationlist.get(mPosition);
            mLocationX = item.getLocationX();
            mLocationY = item.getLocationY();
            mWidth = item.getWidth();
            mHeight = item.getHeight();
            MyProgressDialog mydialog = new MyProgressDialog(this, getResources().getString(R.string.hold_on));
            mydialog.show();
            GlideUtil.showImg(this, mDatas.get(mPosition), imageView, R.mipmap.plugin_camera_no_pictures, new GlideUtil.CbGetImg() {
                @Override
                public void onGet(Bitmap resource) {
                    mydialog.dismiss();
                    imageView.setImageBitmap(resource);
                    imageView.setOriginalInfo(mWidth, mHeight, mLocationX, mLocationY);
                    LayoutParams lp;
                    lp=imageView.getLayoutParams();
                    lp.width=-1;
                    lp.height= -1;
                    imageView.setLayoutParams(lp);
                    imageView.transformIn();
                    imageView.setOnTransformListener(new SmoothImageView.TransformListener(){
                        @Override
                        public void onTransformComplete(int mode) {
                            all.setVisibility(View.VISIBLE);
                        }
                    });
                }
            });
        }else{
            all.setVisibility(View.VISIBLE);
        }
        advPics = new ArrayList<>();
        imageViews = new ImageView[size];
        for(int i = 0;i<size;i++){
            PhotoView img = new PhotoView(this);
            img.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT));
            img.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
                @Override
                public void onViewTap(View view, float x, float y) {
                    all.setVisibility(View.GONE);

                    finish();
                }
            });
            String imgUrl = GlideUtil.getHttpUrl(mDatas.get(i));
            img.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    requestRuntimePermisssions(SD_PERMISSION, new PermissionListener() {
                        @Override
                        public void onGranted() {
                            Pop(imgUrl);
                        }

                        @Override
                        public void onDenied(List<String> deniedList) {
                        }
                    });
                    return false;
                }
            });
            GlideUtil.showImg(this, imgUrl, img);
            advPics.add(img);
            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(new LayoutParams(AllUtils.dip2px(this,15), AllUtils.dip2px(this,15)));
            imageView.setPadding(5, 5, 5, 5);
            imageViews[i] = imageView;
            if (i == 0) {
                imageViews[i].setImageResource(R.mipmap.icon_point_pre);
            } else {
                imageViews[i].setImageResource(R.mipmap.icon_point);
            }
            group.addView(imageViews[i]);
        }

        adapter = new MyPageAdapter(advPics);
        pager.setAdapter(adapter);
        pager.setPageMargin(getResources().getDimensionPixelOffset(R.dimen.dp_10));
        pager.addOnPageChangeListener(pageChangeListener);
        pager.setCurrentItem(mPosition);

    }
    private void Pop(String imgUrl) {
        View view = LayoutInflater.from(this).inflate(R.layout.myqr_pop, null);
        PopupWindow popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        TextView save = (TextView) view.findViewById(R.id.save);
        save.setText("");
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GlideUtil.showImg(Photo.this,imgUrl,null, img -> {
                    if (null != img) {
                        String path = FileUtils.saveBitmap(img);
                        popupWindow.dismiss();
                        
                        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + path)));
                        showResult(getString(R.string.person_qr_save_success)+path);
                    }
                });

            }
        });
        view.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        view.findViewById(R.id.send).setVisibility(View.GONE);
        view.findViewById(R.id.xianView).setVisibility(View.GONE);
        popupWindow.setAnimationStyle(R.style.dialogAnim);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
        popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);

    }
    private OnPageChangeListener pageChangeListener = new OnPageChangeListener() {
        @Override
        public void onPageSelected(int arg0) {
            LocationBean item = mLocationlist.get(arg0);
            mLocationX = item.getLocationX();
            mLocationY = item.getLocationY();
            mWidth = item.getWidth();
            mHeight = item.getHeight();
            for (int i = 0; i < size; i++) {
                imageViews[arg0].setImageResource(R.mipmap.icon_point_pre);
                if (arg0 != i) {
                    imageViews[i].setImageResource(R.mipmap.icon_point);
                }
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    };


    @Override
    public void onBackPressed() {
       
   
    
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            overridePendingTransition(0, 0);
        }
    }

    class MyPageAdapter extends PagerAdapter {
        private ArrayList<View> listViews;
        private int size;

        public MyPageAdapter(ArrayList<View> listViews) {
            this.listViews = listViews;
            size = listViews == null ? 0 : listViews.size();
        }

        public void setListViews(ArrayList<View> listViews) {
            this.listViews = listViews;
            size = listViews == null ? 0 : listViews.size();
        }

        @Override
        public int getCount() {
            return size;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPagerFixed) arg0).removeView(listViews.get(arg1 % size));
        }

        @Override
        public void finishUpdate(View arg0) {
        }

        @Override
        public Object instantiateItem(View arg0, int arg1) {
            try {
                ((ViewPagerFixed) arg0).addView(listViews.get(arg1 % size), 0);
            } catch (Exception e) {
            }
            return listViews.get(arg1 % size);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }
    }
}
