

package common.app.base.base;

import android.app.Activity;
import android.content.Context;

import common.app.im.base.Loading;


public interface BaseFragmentView<T> extends Loading {


    void setPresenter(T presenter);

    
    void showMsg(String msg);

    
    void showMsg(int msg);

    Activity getActivity();

    Context getContext();


}
