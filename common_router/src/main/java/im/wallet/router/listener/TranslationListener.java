package im.wallet.router.listener;

import java.util.concurrent.atomic.AtomicReference;


public interface TranslationListener {
    public AtomicReference<Object> dataARF = new AtomicReference<>();

    
    void onFail(String errorInfo);

    
    default void onSubmitSuccess(){

    }

    
    void onTransSuccess();

    default void setData(Object data){
        dataARF.set(data);
    }

    default Object getData(){
        return dataARF.get();
    }
}
