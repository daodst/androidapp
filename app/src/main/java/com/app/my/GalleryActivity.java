

package com.app.my;




import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;

import com.app.R;
import com.app.my.zoom.PhotoView;
import com.app.my.zoom.ViewPagerFixed;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import common.app.mall.BaseActivity;
import common.app.my.localalbum.bean.LocalFile;
import common.app.ui.view.TitleBarView;
import common.app.utils.GlideUtil;



public class GalleryActivity extends BaseActivity {
    private TitleBarView titleBarView;
    private Intent intent;

    
    
    
    private TextView positionTextView;
    
    private int position;
    
    private int location = 0;

    private ArrayList<View> listViews = new ArrayList<View>();
    private ViewPagerFixed pager;
    private MyPageAdapter adapter;
    private List<LocalFile> tempSelectBitmap = new ArrayList<>();

    public static final String KEY_SHOW_DEL_BTN = "showDelBtn";
    private boolean mShowDelBtn = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setView(R.layout.activity_galler);
    }
    @Override
    protected void initView() {
        super.initView();
        titleBarView = (TitleBarView) findViewById(R.id.title_bar);
        
        pager = (ViewPagerFixed) findViewById(R.id.gallery01);

        mShowDelBtn = getIntent().getBooleanExtra(KEY_SHOW_DEL_BTN, true);
    }
    @Override
    protected void initData() {
        super.initData();
        intent = getIntent();
        tempSelectBitmap = (List<LocalFile>) intent.getSerializableExtra("list");
        if(null==tempSelectBitmap){
            finish();
        }
        titleBarView.setOnTitleBarClickListener(new TitleBarView.TitleBarClickListener() {
            @Override
            public void leftClick() {
                setDestroy();
            }
            @Override
            public void rightClick() {
                if (listViews.size() == 1) {
                    tempSelectBitmap.clear();
                    setDestroy();
                } else {
                    tempSelectBitmap.remove(location);
                    pager.removeAllViews();
                    listViews.remove(location);
                    adapter.setListViews(listViews);
                    
                    adapter.notifyDataSetChanged();
                }
            }
        });
        if (!mShowDelBtn) {
            titleBarView.setRightImgVisable(false);
        }
        

        intent = getIntent();
        position = Integer.parseInt(intent.getStringExtra("position"));
        
        

        pager.addOnPageChangeListener(pageChangeListener);
        listViews.clear();
        for (int i = 0; i < tempSelectBitmap.size(); i++) {
            LocalFile imgitem = tempSelectBitmap.get(i);
            if(imgitem.ishttp()){
                PhotoView img = new PhotoView(this);
                GlideUtil.showImg(this,imgitem.getOriginalUri(),img);
                img.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                        LayoutParams.MATCH_PARENT));
                listViews.add(img);
            }else{
                PhotoView img = new PhotoView(this);
                GlideUtil.showImgSD(this,imgitem.getOriginalUri(),img);
                img.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                        LayoutParams.MATCH_PARENT));
                listViews.add(img);
            }

        }
        adapter = new MyPageAdapter(listViews);
        pager.setAdapter(adapter);
        pager.setPageMargin((int)getResources().getDimensionPixelOffset(R.dimen.dp_10));
        int id = intent.getIntExtra("ID", 0);
        pager.setCurrentItem(id);
    }

    private void setDestroy() {
        intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("list", (Serializable) tempSelectBitmap);
        intent.putExtras(bundle);
        setResult(RESULT_OK,intent);
        finish();

    }

    private OnPageChangeListener pageChangeListener = new OnPageChangeListener() {
        @Override
        public void onPageSelected(int arg0) {
            location = arg0;
        }
        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }
        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    };

    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setDestroy();
        }
        return true;
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
