

package common.app.base.view.bannervew;

import android.os.Parcelable;
import android.view.View;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.List;



public class MenuPagerAdapter extends PagerAdapter {
    public List<View> mListViews;

    public MenuPagerAdapter(List<View> mListViews) {
        this.mListViews = mListViews;
    }

    @Override
    public int getCount() {
        return mListViews.size();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public int getItemPosition(Object object) {
        
        return super.getItemPosition(object);
    }

    @Override
    public void destroyItem(View arg0, int arg1, Object arg2) {
        
        try {
            ((ViewPager) arg0).removeView(mListViews.get(arg1));
        }catch (Exception e){

        }
    }

    @Override
    public Object instantiateItem(View arg0, int arg1) {
        
        try {
            ((ViewPager) arg0).addView(mListViews.get(arg1));
            return mListViews.get(arg1);
        }catch (Exception e){
            return null;
        }

    }

    @Override
    public void restoreState(Parcelable arg0, ClassLoader arg1) {
        

    }

    @Override
    public Parcelable saveState() {
        
        return null;
    }

    @Override
    public void startUpdate(View arg0) {
        

    }

    @Override
    public void finishUpdate(View arg0) {
        

    }
}
