

package common.app.base.view.bannervew;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;

import java.util.ArrayList;
import java.util.List;

import common.app.R;
import common.app.base.fragment.mall.model.AdvertEntity;
import common.app.utils.AllUtils;



public class ImageCycleView extends LinearLayout {
    private List<AdvertEntity> infoList;
    
    private Context mContext;

    
    private CycleViewPager mBannerPager = null;

    
    private ImageCycleAdapter mAdvAdapter;

    
    private ViewGroup mGroup;

    
    private ImageView mImageView = null;

    
    private ImageView[] mImageViews = null;

    
    private int mImageIndex = 1;

    
    private float mScale;

    
    public ImageCycleView(Context context) {
        super(context);
    }
    private touchListener mTouchListener;
    
    public ImageCycleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mScale = context.getResources().getDisplayMetrics().density;
        LayoutInflater.from(context).inflate(R.layout.view_banner_content, this);
        mBannerPager = (CycleViewPager) findViewById(R.id.pager_banner);
        mBannerPager.setOnPageChangeListener(new GuidePageChangeListener());
        mBannerPager.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        
                        startImageTimerTask();
                        break;
                    default:
                        
                        stopImageTimerTask();
                        break;
                }
                return false;
            }
        });
        
        mGroup = (ViewGroup) findViewById(R.id.viewGroup);
    }

    
    public void setImageResources(List<AdvertEntity> infoList, ImageCycleViewListener imageCycleViewListener) {
        
        mGroup.removeAllViews();
        
        this.infoList=infoList;
        final int imageCount = infoList.size();
        mImageViews = new ImageView[imageCount];
        for (int i = 0; i < imageCount; i++) {
            mImageView = new ImageView(mContext);
            int imageParams = (int) (mScale * 20 + 0.5f);
            int imagePadding = (int) (mScale * 5 + 0.5f);
            LayoutParams layout = new LayoutParams(AllUtils.dip2px(mContext,5), AllUtils.dip2px(mContext,5));
            layout.setMargins(12, 0, 12, 0);
            mImageView.setLayoutParams(layout);
            
            mImageViews[i] = mImageView;
            if (i == 0) {
                mImageViews[i].setBackgroundResource(R.mipmap.banner_notcheck);
            } else {
                mImageViews[i].setBackgroundResource(R.mipmap.banner_check);
            }
            mGroup.addView(mImageViews[i]);
        }
        mAdvAdapter = new ImageCycleAdapter(mContext, infoList, imageCycleViewListener);
        mBannerPager.setAdapter(mAdvAdapter);
        startImageTimerTask();
    }

    
    public void startImageCycle() {
        startImageTimerTask();
    }

    
    public void pushImageCycle() {
        stopImageTimerTask();
    }

    
    private void startImageTimerTask() {
        stopImageTimerTask();
        
        mHandler.postDelayed(mImageTimerTask, 3000);
    }

    
    private void stopImageTimerTask() {
        mHandler.removeCallbacks(mImageTimerTask);
    }

    private Handler mHandler = new Handler();

    
    private Runnable mImageTimerTask = new Runnable() {

        @Override
        public void run() {
            if (mImageViews != null) {
                
                if ((++mImageIndex) == mImageViews.length + 1) {
                    mImageIndex = 1;
                }
                mBannerPager.setCurrentItem(mImageIndex);
            }
        }
    };

    
    private final class GuidePageChangeListener implements OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int state) {
            if (state == ViewPager.SCROLL_STATE_IDLE)
                startImageTimerTask(); 
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int index) {

            if (index == 0 || index == mImageViews.length + 1) {
                return;
            }
            
            mImageIndex = index;
            index -= 1;
            mImageViews[index].setBackgroundResource(R.mipmap.banner_check);
            for (int i = 0; i < mImageViews.length; i++) {
                if (index != i) {
                    mImageViews[i].setBackgroundResource(R.mipmap.banner_notcheck);
                }
            }

        }

    }

    private class ImageCycleAdapter extends PagerAdapter {

        
        private ArrayList<ImageView> mImageViewCacheList;

        
        private List<AdvertEntity> mAdList = new ArrayList<AdvertEntity>();

        
        private ImageCycleViewListener mImageCycleViewListener;


        private Context mContext;

        public ImageCycleAdapter(Context context, List<AdvertEntity> adList, ImageCycleViewListener imageCycleViewListener) {
            mContext = context;
            mAdList = adList;
            mImageCycleViewListener = imageCycleViewListener;
            mImageViewCacheList = new ArrayList<ImageView>();
        }

        @Override
        public int getCount() {
            return mAdList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            if (null == mAdList || mAdList.size()==0) {
                return null;
            }
            String imageUrl = mAdList.get(position).getImage();
            ImageView imageView = null;
            
            if (mImageViewCacheList.isEmpty()) {
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new LayoutParams(AllUtils.dip2px(mContext,8), AllUtils.dip2px(mContext,8)));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            } else {
                imageView = mImageViewCacheList.remove(0);
            }
            
            imageView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    mImageCycleViewListener.onImageClick(mAdList.get(position), position, v);
                }
            });
            imageView.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return false;
                }
            });
            container.addView(imageView);
            mImageCycleViewListener.displayImage(imageUrl, imageView);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ImageView view = (ImageView) object;
            container.removeView(view);
            mImageViewCacheList.add(view);
        }

    }

    
    public static interface touchListener {
        public void touchListener(boolean bool);
    }
    public void setTouchListener(touchListener touchListener){
        this.mTouchListener=touchListener;
    }
    
    public static interface ImageCycleViewListener {

        
        public void displayImage(String imageURL, ImageView imageView);

        
        public void onImageClick(AdvertEntity info, int postion, View imageView);
    }

}
