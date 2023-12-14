

package common.app.ui.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import common.app.R;


public class ToastView {
	
	public Toast toast;
	private int time;
	private Timer timer;
	private ImageView mImag;
	
	public ToastView(Context context, String text) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.toast_view, null);
		TextView t = (TextView) view.findViewById(R.id.toast_text);
		mImag = (ImageView) view.findViewById(R.id.toast_img);

		t.setText(text);
		if(toast != null) {
			toast.cancel();
		}
		toast = new Toast(context);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setView(view);
	}
	
	
	
	public void setGravity(int gravity, int xOffset, int yOffset) {
		
		toast.setGravity(gravity, xOffset, yOffset);
	}

	public void hideImg() {
    if (null != mImag) {
        mImag.setVisibility(View.GONE);
    }
	}
	
	
	public void setDuration(int duration) {
		toast.setDuration(duration);
	}
	
	
	public void setLongTime(int duration) {
		
		time = duration;
		timer = new Timer();
        timer.schedule(new TimerTask(){
        	@Override
        	public void run() {
        		 
        		if(time-1000 >= 0) {
        			show();
        			time= time - 1000;
        		} else {
        			timer.cancel();
        		}
        	}
        }, 0, 1000);
	}
	
	public void show() {
		toast.show();
	}
	
	public void cancel() {
		if(toast != null) {
			toast.cancel();
		}
	}

}
