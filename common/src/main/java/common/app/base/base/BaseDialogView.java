

package common.app.base.base;

import android.app.Activity;
import android.content.Context;


public interface BaseDialogView<T>  {


    void setPresenter(T presenter);

    
    void showMsg(String msg);

    
    void showMsg(int msg);

    Activity getActivity();

    Context getContext();

}
