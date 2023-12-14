

package com.wallet.ctc.view.dialog.demo;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.wallet.ctc.R;
import com.wallet.ctc.util.LogUtil;
import com.wallet.ctc.view.wheelview.OnItemSelectedListener;
import com.wallet.ctc.view.wheelview.WheelAdapter;
import com.wallet.ctc.view.wheelview.WheelView;

public class TimePickerDialog extends Dialog{
	private Activity mContext = null;
	private View parentView= null;
	private WheelAdapter wheelAdapter;
	private int position=0;
	public TimePickerDialog(Activity context,WheelAdapter wheelAdapter) {
		super(context, R.style.transparentFrameWindowStyle);
		mContext = context;
		this.wheelAdapter=wheelAdapter;
	}

	public static int dip2px(float dip, Context context) {
		DisplayMetrics me = context.getResources().getDisplayMetrics();
		int margin = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, dip, me);
		return margin;
	}


	
	public void showDialog() {
		if ( parentView== null) {
			parentView=   getLayoutInflater().inflate(
					R.layout.dialog_statistics, null);
		}
		Button sub= (Button)parentView .findViewById(R.id.btn_sub);
		Button cancel= (Button)parentView .findViewById(R.id.btn_cancel);
		WheelView content=(WheelView)parentView.findViewById(R.id.content);
		content.setAdapter(wheelAdapter);
		cancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dismiss();
			}
		});
		content.setCurrentItem(position);
		sub.setOnClickListener(new MyOnclick());
		content.setOnItemSelectedListener(new OnItemSelectedListener(){
			@Override
			public void onItemSelected(int index) {
				position=index;
			}
		});
		LinearLayout.LayoutParams mLayoutParams = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, dip2px(300, mContext));
		mLayoutParams.gravity = Gravity.CENTER_VERTICAL;
		parentView.setLayoutParams(mLayoutParams);
		setContentView(parentView);
		setShowPosition();
		show();
	}
	
	@SuppressWarnings("deprecation")
	private void setShowPosition() {
		Window window = getWindow();
		
		window.setWindowAnimations(R.style.main_menu_animstyle);
		WindowManager.LayoutParams wl = window.getAttributes();
		wl.x = 0;
		wl.y = mContext.getWindowManager().getDefaultDisplay().getHeight();
		
		wl.width = LayoutParams.MATCH_PARENT;
		wl.height = LayoutParams.WRAP_CONTENT;
		
		onWindowAttributesChanged(wl);
		
		setCanceledOnTouchOutside(true);
	}

	public class MyOnclick implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			LogUtil.d(""+position);
		}
	}
}
