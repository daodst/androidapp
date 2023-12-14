

package common.app.base;

import android.app.Application;
import android.os.Looper;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.util.HashMap;
import java.util.Map;


public class BaseViewModel extends AndroidViewModel implements IBase.IUiBase {


    
    public BaseViewModel(@NonNull Application application) {
        super(application);
        initUIControl();
    }

    
    private LifecycleOwner lifecyleOwner;

    
    public LifecycleOwner getLifecyleOwner() {
        return lifecyleOwner;
    }

    
    public void setLifecyleOwner(LifecycleOwner lifecyleOwner) {
        this.lifecyleOwner = lifecyleOwner;
    }

    
    private LifecycleProvider lifecycle;
    private boolean isFragment;

    
    public void injectLifecycleProvider(LifecycleProvider lifecycle, boolean isFragment) {
        this.lifecycle = lifecycle;
        this.isFragment = isFragment;
    }

    public LifecycleProvider getLifecycleProvider() {
        return lifecycle;
    }

    public LifecycleTransformer getTrasnform() {
        if (null != lifecycle) {
            if (isFragment) {
                return lifecycle.bindUntilEvent(FragmentEvent.DESTROY);
            } else {
                return lifecycle.bindUntilEvent(ActivityEvent.DESTROY);
            }

        } else {
            return null;
        }
    }


    
    public Map<String, MutableLiveData> mDataContainers = new HashMap<>();


    
    private <T> BaseViewModel observe(LiveData<T> liveData, @NonNull LifecycleOwner owner, @NonNull Observer<T> observer) {
        liveData.observe(owner, observer);
        return this;
    }

    
    public <T> BaseViewModel observe(LiveData<T> liveData, @NonNull Observer<T> observer) {
        return observe(liveData, getLifecyleOwner(), observer);
    }


    
    protected LiveDataEvent createLiveData(LiveDataEvent liveDataEvent) {
        return liveDataEvent = new LiveDataEvent<>();
    }


    
    
    protected LiveDataEvent<DialogContainerEvent> showDialogEvent;
    
    protected LiveDataEvent dismissDialogEvent;

    
    protected LiveDataEvent<String> showToastEvent;

    
    protected LiveDataEvent<String> showLoadingEvent;
    
    protected LiveDataEvent dismissLoadingEvent;

    
    private void initUIControl() {
        showDialogEvent = createLiveData(showDialogEvent);
        dismissDialogEvent = createLiveData(dismissDialogEvent);
        showToastEvent = createLiveData(showToastEvent);
        showLoadingEvent = createLiveData(showLoadingEvent);
        dismissLoadingEvent = createLiveData(dismissLoadingEvent);
    }

    
    protected boolean isMainThread() {
        return Thread.currentThread() == Looper.getMainLooper().getThread();
    }

    
    protected <T> void notifyValue(T value, LiveDataEvent<T> liveDataEvent) {
        if (isMainThread()) {
            liveDataEvent.setValue(value);
        } else {
            liveDataEvent.postValue(value);
        }
    }

    @Override
    public void showAlertDialog(String content, View.OnClickListener okBtnListener, View.OnClickListener cancelBtnListener) {
        DialogContainerEvent dialogEvent = new DialogContainerEvent();
        dialogEvent.content = content;
        dialogEvent.cancleBtnListener = cancelBtnListener;
        dialogEvent.okBtnListener = okBtnListener;
        notifyValue(dialogEvent, showDialogEvent);
    }

    @Override
    public void dismissDialog() {
        notifyValue(null, dismissDialogEvent);
    }

    @Override
    public void showToast(String content) {
        notifyValue(content, showToastEvent);
    }

    @Override
    public void showLoadingDialog(String content) {
        notifyValue(content, showLoadingEvent);
    }

    @Override
    public void dismissLoadingDialog() {
        notifyValue(null, dismissLoadingEvent);
    }

    


}
