

package common.app.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

import common.app.mall.Pullable;


public class PullableListView extends ListView implements Pullable
{

	private boolean pd = true;

	private boolean mCanPullDown = true;
	private boolean mCanPullUp = true;


	public PullableListView(Context context)
	{
		super(context);
	}

	public PullableListView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public PullableListView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	
	public void setNo(boolean pd){
		this.pd = pd;
	}

	
	public void setCanPullDown(boolean canPullDown) {
		this.mCanPullDown = canPullDown;
	}

	public void setCanPullUp(boolean canPullUp) {
		this.mCanPullUp = canPullUp;
	}

	@Override
	public boolean canPullDown()
	{
		if(mCanPullDown){
			if (getCount() == 0||null==getChildAt(0))
			{
				
				return true;
			} else if (getFirstVisiblePosition() == 0
					&& getChildAt(0).getTop() >= 0)
			{
				
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean canPullUp()
	{
		if (mCanPullUp) {
			if (getCount() == 0)
			{
				
				return true;
			} else if (getLastVisiblePosition() == (getCount() - 1))
			{
				
				if (getChildAt(getLastVisiblePosition() - getFirstVisiblePosition()) != null
						&& getChildAt(
						getLastVisiblePosition()
								- getFirstVisiblePosition()).getBottom() <= getMeasuredHeight())
					return true;
			}
		}
		return false;
	}
}
