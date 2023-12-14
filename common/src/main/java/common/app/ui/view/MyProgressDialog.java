

package common.app.ui.view;


import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import common.app.R;


public class MyProgressDialog {
	
	public Dialog mDialog;
	private AnimationDrawable animationDrawable = null;

	private TextView mMsg;
	
	public MyProgressDialog(Context context, String message) {

		View view = LayoutInflater.from(context).inflate(R.layout.progress_view, null);

		mMsg = (TextView) view.findViewById(R.id.progress_messagess);
		mMsg.setText(message);
		ImageView loadingImage = (ImageView) view.findViewById(R.id.progress_view);
		loadingImage.setImageResource(R.drawable.loading_animation);
		animationDrawable = (AnimationDrawable)loadingImage.getDrawable();

		mDialog = new Dialog(context, R.style.dialog);
		mDialog.setContentView(view);
		mDialog.setCanceledOnTouchOutside(false);
		
	}

	public void setMsg(String messge){
		mMsg.setText(messge);
	}
	
	public void show() {
		try {
			mDialog.show();
			if(animationDrawable!=null){
				animationDrawable.setOneShot(false);
				animationDrawable.start();
			}
		}catch (Exception e){

		}
	}
	
	public void setCanceledOnTouchOutside(boolean cancel) {
		mDialog.setCanceledOnTouchOutside(cancel);
	}
	
	public void dismiss() {
		if(mDialog.isShowing()) {
			try {
				mDialog.dismiss();
				animationDrawable.stop();
			}catch (Exception e){

			}
		}
	}
    public boolean isShowing(){
        if(mDialog.isShowing()) {
            return true;
        }
        return false;
    }
}
