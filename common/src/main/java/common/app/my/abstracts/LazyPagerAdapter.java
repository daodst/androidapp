

package common.app.my.abstracts;




import android.util.SparseArray;
import android.view.ViewGroup;

import androidx.viewpager.widget.PagerAdapter;


public abstract class LazyPagerAdapter<T> extends PagerAdapter {

    protected SparseArray<T> mLazyItems = new SparseArray<T>();
    private T mCurrentItem;

    
    public abstract T addLazyItem(ViewGroup container, int position);

    
    protected abstract T getItem(ViewGroup container, int position);

    
    public boolean isLazyItem(int position) {
        return mLazyItems.get(position) != null;
    }

    
    public T getCurrentItem() {
        return mCurrentItem;
    }


    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        mCurrentItem = addLazyItem(container, position);
    }

}
