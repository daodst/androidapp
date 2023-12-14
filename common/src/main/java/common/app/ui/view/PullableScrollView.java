

package common.app.ui.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ScrollView;

import common.app.mall.Pullable;

public class PullableScrollView extends ScrollView implements Pullable
{

	public PullableScrollView(Context context)
	{
		super(context);
	}

	public PullableScrollView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public PullableScrollView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}
	
	public interface ScrollViewListener {

		void onScrollChanged(PullableScrollView observableScrollView, int x, int y, int oldx, int oldy);
	}
	private ScrollViewListener mScrollViewListener=null;

	public void setScrollViewListener(ScrollViewListener scrollViewListener) {
		mScrollViewListener = scrollViewListener;
	}
	@Override
	protected void onScrollChanged(int x, int y, int oldx, int oldy) {
		super.onScrollChanged(x, y, oldx, oldy);
		if (mScrollViewListener != null) {
			mScrollViewListener.onScrollChanged(this, x, y, oldx, oldy);
		}
	}

	@Override
	public boolean canPullDown()
	{
		if (getScrollY() == 0)
			return true;
		else
			return false;
	}

	@Override
	public boolean canPullUp()
	{
		if (getScrollY() >= (getChildAt(0).getHeight() - getMeasuredHeight()))
			return true;
		else
			return false;
	}

	
	@Override
	protected int computeScrollDeltaToGetChildRectOnScreen(Rect rect) {
		return 0;
		
	}
}
