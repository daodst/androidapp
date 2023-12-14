

package common.app.ui.view;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import common.app.utils.AllUtils;



public class FloatViewHelper {
    private static final String TAG = "FloatViewHelper";
    private static FloatView mFloatView;
    private static List<String> notShowList = new ArrayList();
    static {
        
        notShowList.add("ApiSettingActivity");
    }

    public static void showFloatView(final Context context){

        if(!AllUtils.isAppOnForeground(context)|| AllUtils.isTargetRunningForeground(context,notShowList)){
            if(mFloatView != null){
                mFloatView.dismiss();
            }
            return;
        }
        if(mFloatView == null){
            mFloatView = new FloatView(context.getApplicationContext());
        }
        if(!mFloatView.isShown()){
            mFloatView.show();
        }
    }

    public static void removeFloatView(Context context){
        if(mFloatView ==null||mFloatView.getWindowToken()==null){
            return;
        }
        mFloatView.dismiss();
    }
    public static void addFilterActivities(List<String> activityNames){
        notShowList.addAll(activityNames);
    }
}
