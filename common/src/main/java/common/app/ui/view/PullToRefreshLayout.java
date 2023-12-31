

package common.app.ui.view;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import common.app.R;
import common.app.mall.Pullable;
import common.app.utils.AllUtils;




public class PullToRefreshLayout extends RelativeLayout
{
	public static final String TAG = "PullToRefreshLayout";
	
	public static final int INIT = 0;
	
	public static final int RELEASE_TO_REFRESH = 1;
	
	public static final int REFRESHING = 2;
	
	public static final int RELEASE_TO_LOAD = 3;
	
	public static final int LOADING = 4;
	
	public static final int DONE = 5;
	
	private int state = INIT;
	
	private OnRefreshListener mListener;
	
	public static final int SUCCEED = 0;
	
	public static final int FAIL = 1;
	
	private float downY, lastY;
	
	private float downX, lastX;

	
	public float pullDownY = 0;
	
	private float pullUpY = 0;

	
	private float refreshDist = 200;
	
	private float loadmoreDist = 200;

	private MyTimer timer;
	
	public float MOVE_SPEED = 8;
	
	private boolean isLayout = false;
	
	private boolean isTouch = false;
	
	private float radio = 2;

	
	private RotateAnimation rotateAnimation;
	
	private RotateAnimation refreshingAnimation;

	
	private View refreshView;
	
	private View pullView;
	
	private View refreshingView;
	
	private View refreshStateImageView;
	
	private TextView refreshStateTextView;
	
	private RelativeLayout head_view;

	
	private View loadmoreView;
	
	private View pullUpView;
	
	private View loadingView;
	
	private View loadStateImageView;
	
	private TextView loadStateTextView;

	
	private View pullableView;
	
	private int mEvents;
	
	private boolean canPullDown = true;
	private boolean canPullUp = true;
	private int PullUp = 0;

	private Context mContext;
	private View bg;
	private int Width,Height;
	private ScaleAnimation animation;
	
	Handler updateHandler = new Handler()
	{

		@Override
		public void handleMessage(Message msg)
		{
			
			MOVE_SPEED = (float) (8 + 5 * Math.tan(Math.PI / 2
					/ getMeasuredHeight() * (pullDownY + Math.abs(pullUpY))));
			if (!isTouch)
			{
				
				if (state == REFRESHING && pullDownY <= refreshDist)
				{
					pullDownY = refreshDist;
					timer.cancel();
				} else if (state == LOADING && -pullUpY <= loadmoreDist)
				{
					pullUpY = -loadmoreDist;
					timer.cancel();
				}

			}
			if (pullDownY > 0) {
				pullDownY -= MOVE_SPEED;
			} else if (pullUpY < 0) {
				pullUpY += MOVE_SPEED;
			}
			if (pullDownY < 0)
			{
				
				pullDownY = 0;
				pullView.clearAnimation();
				
				if (state != REFRESHING && state != LOADING) {
					changeState(INIT);
				}
				timer.cancel();
				requestLayout();
			}
			if (pullUpY > 0)
			{
				
				pullUpY = 0;
				pullUpView.clearAnimation();
				
				if (state != REFRESHING && state != LOADING) {
					changeState(INIT);
				}
				timer.cancel();
				requestLayout();
			}
			Log.d("handle", "handle");
			
			requestLayout();
			
			if (pullDownY + Math.abs(pullUpY) == 0) {
				timer.cancel();
			}
		}

	};

	public void setOnRefreshListener(OnRefreshListener listener)
	{
		mListener = listener;
	}

	public PullToRefreshLayout(Context context)
	{
		super(context);
		initView(context);
	}

	public PullToRefreshLayout(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		initView(context);
	}

	public PullToRefreshLayout(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		initView(context);
	}

	private void initView(Context context)
	{
		mContext = context;
		timer = new MyTimer(updateHandler);
		rotateAnimation = (RotateAnimation) AnimationUtils.loadAnimation(
				context, R.anim.reverse_anim);
		refreshingAnimation = (RotateAnimation) AnimationUtils.loadAnimation(
				context, R.anim.rotating);

		
		LinearInterpolator lir = new LinearInterpolator();
		rotateAnimation.setInterpolator(lir);
		refreshingAnimation.setInterpolator(lir);
		Width = AllUtils.getDisplayMetricsWidth((WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE));
		Height = AllUtils.getDisplayMetricsHeight((WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE));

	}

	private void hide()
	{
		timer.schedule(5);
	}

	
	
	public void refreshFinish(int refreshResult)
	{
		if(refreshingView==null){
			return;
		}
		refreshingView.clearAnimation();
		refreshingView.setVisibility(View.GONE);
		switch (refreshResult)
		{
			case SUCCEED:
				
				refreshStateImageView.setVisibility(View.VISIBLE);
				refreshStateTextView.setText(R.string.refresh_succeed);
				refreshStateImageView
						.setBackgroundResource(R.mipmap.refresh_succeed);
				
				refreshStateTextView.setTextColor(getResources().getColor(R.color.white));
				bg.setBackgroundResource(R.color.shuaxinbgs);
				animation =new ScaleAnimation(0.0f, Width, 1.0f, 1.0f,
						Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 1.0f);
				animation.setDuration(500);
				animation.setFillAfter(true);
				bg.startAnimation(animation);


				break;
			case FAIL:
			default:
				
				refreshStateImageView.setVisibility(View.VISIBLE);
				refreshStateTextView.setText(R.string.refresh_fail);
				refreshStateImageView
						.setBackgroundResource(R.mipmap.refresh_failed);
				
				refreshStateTextView.setTextColor(getResources().getColor(R.color.white));
				bg.setBackgroundResource(R.color.gray);

				break;
		}
		if (pullDownY > 0)
		{
			
			new Handler()
			{
				@Override
				public void handleMessage(Message msg)
				{
					changeState(DONE);
					hide();
				}
			}.sendEmptyMessageDelayed(0, 500);
		} else
		{
			changeState(DONE);
			hide();
		}
	}

	
	public void loadmoreFinish(int refreshResult)
	{
		if(loadmoreView==null || null == loadingView){
			return;
		}
		loadingView.clearAnimation();
		loadingView.setVisibility(View.GONE);
		switch (refreshResult)
		{
			case SUCCEED:
				
				loadStateImageView.setVisibility(View.VISIBLE);
				loadStateTextView.setText(R.string.load_succeed);
				loadStateImageView.setBackgroundResource(R.mipmap.load_succeed);
				break;
			case FAIL:
			default:
				
				loadStateImageView.setVisibility(View.VISIBLE);
				loadStateTextView.setText(R.string.load_fail);
				loadStateImageView.setBackgroundResource(R.mipmap.load_failed);
				break;
		}
		if (pullUpY < 0)
		{
			
			new Handler()
			{
				@Override
				public void handleMessage(Message msg)
				{
					changeState(DONE);
					hide();
				}
			}.sendEmptyMessageDelayed(0, 500);
		} else
		{
			changeState(DONE);
			hide();
		}
	}

	private void changeState(int to)
	{
		state = to;
		switch (state)
		{
			case INIT:
				
				refreshStateImageView.setVisibility(View.GONE);
				refreshStateTextView.setText(R.string.pull_to_refresh);
				pullView.clearAnimation();
				pullView.setVisibility(View.VISIBLE);
				
				
				loadStateImageView.setVisibility(View.GONE);
				loadStateTextView.setText(R.string.pullup_to_load);
				pullUpView.clearAnimation();
				pullUpView.setVisibility(View.VISIBLE);
				bg.clearAnimation();
				bg.setBackgroundResource(R.color.shuaxinbg_ones);
				refreshStateTextView.setTextColor(getResources().getColor(R.color.textxiaos));
				break;
			case RELEASE_TO_REFRESH:
				
				refreshStateTextView.setText(R.string.release_to_refresh);
				pullView.startAnimation(rotateAnimation);
				break;
			case REFRESHING:
				
				pullView.clearAnimation();
				refreshingView.setVisibility(View.VISIBLE);
				pullView.setVisibility(View.INVISIBLE);
				refreshingView.startAnimation(refreshingAnimation);
				refreshStateTextView.setText(R.string.refreshing);
				refreshStateTextView.setTextColor(getResources().getColor(R.color.white));
				break;
			case RELEASE_TO_LOAD:
				
				loadStateTextView.setText(R.string.release_to_load);
				pullUpView.startAnimation(rotateAnimation);
				break;
			case LOADING:
				
				pullUpView.clearAnimation();
				loadingView.setVisibility(View.VISIBLE);
				pullUpView.setVisibility(View.INVISIBLE);
				loadingView.startAnimation(refreshingAnimation);
				loadStateTextView.setText(R.string.loading);
				break;
			case DONE:
				
				break;
		}
	}

	
	private void releasePull()
	{
		if(PullUp==0) {
			canPullDown = true;
			canPullUp = true;
		}else if(PullUp==2){
			canPullDown = true;
		}else if(PullUp==3){
			canPullUp = true;
		}
	}
	
	public void releaseCanPull()
	{
		PullUp=0;
		canPullDown = true;
		canPullUp = true;
	}
	
	public void releaseNotPull()
	{
		PullUp=1;
		canPullDown = false;
		canPullUp = false;
	}

	
	public void onlyPullDown()
	{
		PullUp=2;
		canPullDown = true;
		canPullUp = false;
	}
	
	public void onlyPullUp()
	{
		PullUp=3;
		canPullDown = false;
		canPullUp = true;
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev)
	{
		switch (ev.getActionMasked())
		{
			case MotionEvent.ACTION_DOWN:
				downY = ev.getY();
				downX = ev.getX();
				lastY = downY;
				lastX = downX;
				timer.cancel();
				mEvents = 0;
				releasePull();
				break;
			case MotionEvent.ACTION_POINTER_DOWN:
			case MotionEvent.ACTION_POINTER_UP:
				
				mEvents = -1;
				break;
			case MotionEvent.ACTION_MOVE:
				float moveX = ev.getX() - downX;
				float moveY = ev.getY() - downY;
				if(Math.abs(moveX)>Math.abs(moveY)){
				}else {
					if (mEvents == 0) {
						if (pullDownY > 0
								|| (((Pullable) pullableView).canPullDown()
								&& canPullDown && state != LOADING)) {
							
							
							pullDownY = pullDownY + (ev.getY() - lastY) / radio;
							if (pullDownY < 0) {
								pullDownY = 0;
								canPullDown = false;
								if(PullUp==0) {
									canPullUp = true;
								}
							}
							if (pullDownY > getMeasuredHeight()) {
								pullDownY = getMeasuredHeight();
							}
							if (state == REFRESHING) {
								
								isTouch = true;
							}
						} else if (pullUpY < 0
								|| (((Pullable) pullableView).canPullUp() && canPullUp && state != REFRESHING)) {
							
							pullUpY = pullUpY + (ev.getY() - lastY) / radio;
							if (pullUpY > 0) {
								pullUpY = 0;
								canPullDown = true;
								canPullUp = false;
							}
							if (pullUpY < -getMeasuredHeight()) {
								pullUpY = -getMeasuredHeight();
							}
							if (state == LOADING) {
								
								isTouch = true;
							}
						} else {
							releasePull();
						}
					} else {
						mEvents = 0;
					}
					lastY = ev.getY();
					
					radio = (float) (2 + 2 * Math.tan(Math.PI / 2 / getMeasuredHeight()
							* (pullDownY + Math.abs(pullUpY))));
					if (pullDownY > 0 || pullUpY < 0) {
						requestLayout();
					}
					if (pullDownY > 0) {
						if (pullDownY <= refreshDist
								&& (state == RELEASE_TO_REFRESH || state == DONE)) {
							
							changeState(INIT);
						}
						if (pullDownY >= refreshDist && state == INIT) {
							
							changeState(RELEASE_TO_REFRESH);
						}
					} else if (pullUpY < 0) {
						
						if (-pullUpY <= loadmoreDist
								&& (state == RELEASE_TO_LOAD || state == DONE)) {
							changeState(INIT);
						}
						
						if (-pullUpY >= loadmoreDist && state == INIT) {
							changeState(RELEASE_TO_LOAD);
						}

					}
					
					
					if ((pullDownY + Math.abs(pullUpY)) > 8) {
						
						ev.setAction(MotionEvent.ACTION_CANCEL);
					}
				}
				break;
			case MotionEvent.ACTION_UP:
				if (pullDownY > refreshDist || -pullUpY > loadmoreDist)
				
				{
					isTouch = false;
				}
				if (state == RELEASE_TO_REFRESH)
				{
					changeState(REFRESHING);
					
					if (mListener != null) {
						mListener.onRefresh(this);
					}
				} else if (state == RELEASE_TO_LOAD)
				{
					changeState(LOADING);
					
					if (mListener != null) {
						mListener.onLoadMore(this);
					}
				}
				hide();
			default:
				break;
		}
		
		super.dispatchTouchEvent(ev);
		return true;
	}

	
	private class AutoRefreshAndLoadTask extends
			AsyncTask<Integer, Float, String>
	{

		@Override
		protected String doInBackground(Integer... params)
		{
			while (pullDownY < 4 / 3 * refreshDist)
			{
				pullDownY += MOVE_SPEED;
				publishProgress(pullDownY);
				try
				{
					Thread.sleep(params[0]);
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result)
		{
			changeState(REFRESHING);
			
			if (mListener != null) {
				mListener.onRefresh(PullToRefreshLayout.this);
			}
			hide();
		}

		@Override
		protected void onProgressUpdate(Float... values)
		{
			if (pullDownY > refreshDist) {
				changeState(RELEASE_TO_REFRESH);
			}
			requestLayout();
		}

	}

	
	public void autoRefresh()
	{
		AutoRefreshAndLoadTask task = new AutoRefreshAndLoadTask();
		task.execute(20);
	}

	
	public void autoLoad()
	{
		pullUpY = -loadmoreDist;
		requestLayout();
		changeState(LOADING);
		
		if (mListener != null) {
			mListener.onLoadMore(this);
		}
	}

	private void initView()
	{
		
		pullView = refreshView.findViewById(R.id.pull_icon);
		refreshStateTextView = (TextView) refreshView
				.findViewById(R.id.state_tv);
		refreshingView = refreshView.findViewById(R.id.refreshing_icon);
		refreshStateImageView = refreshView.findViewById(R.id.state_iv);
		head_view = (RelativeLayout) refreshView.findViewById(R.id.head_view);
		
		pullUpView = loadmoreView.findViewById(R.id.pullup_icon);
		loadStateTextView = (TextView) loadmoreView
				.findViewById(R.id.loadstate_tv);
		loadingView = loadmoreView.findViewById(R.id.loading_icon);
		loadStateImageView = loadmoreView.findViewById(R.id.loadstate_iv);
		bg = refreshView.findViewById(R.id.bg);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b)
	{
		Log.d("Test", "Test");
		if (!isLayout)
		{
			
			refreshView = getChildAt(0);
			pullableView = getChildAt(1);
			loadmoreView = getChildAt(2);
			isLayout = true;
			initView();
			refreshDist = ((ViewGroup) refreshView).getChildAt(0)
					.getMeasuredHeight();
			loadmoreDist = ((ViewGroup) loadmoreView).getChildAt(0)
					.getMeasuredHeight();
		}
		
		refreshView.layout(0,
				(int) (pullDownY + pullUpY) - refreshView.getMeasuredHeight(),
				refreshView.getMeasuredWidth(), (int) (pullDownY + pullUpY));
		pullableView.layout(0, (int) (pullDownY + pullUpY),
				pullableView.getMeasuredWidth(), (int) (pullDownY + pullUpY)
						+ pullableView.getMeasuredHeight());
		loadmoreView.layout(0,
				(int) (pullDownY + pullUpY) + pullableView.getMeasuredHeight(),
				loadmoreView.getMeasuredWidth(),
				(int) (pullDownY + pullUpY) + pullableView.getMeasuredHeight()
						+ loadmoreView.getMeasuredHeight());
	}

	class MyTimer
	{
		private Handler handler;
		private Timer timer;
		private MyTask mTask;

		public MyTimer(Handler handler)
		{
			this.handler = handler;
			timer = new Timer();
		}

		public void schedule(long period)
		{
			if (mTask != null)
			{
				mTask.cancel();
				mTask = null;
			}
			mTask = new MyTask(handler);
			timer.schedule(mTask, 0, period);
		}

		public void cancel()
		{
			if (mTask != null)
			{
				mTask.cancel();
				mTask = null;
			}
		}

		class MyTask extends TimerTask
		{
			private Handler handler;

			public MyTask(Handler handler)
			{
				this.handler = handler;
			}

			@Override
			public void run()
			{
				handler.obtainMessage().sendToTarget();
			}

		}
	}

	
	public interface OnRefreshListener
	{
		
		void onRefresh(PullToRefreshLayout pullToRefreshLayout);

		
		void onLoadMore(PullToRefreshLayout pullToRefreshLayout);
	}

}
