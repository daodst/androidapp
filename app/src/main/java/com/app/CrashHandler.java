

package com.app;

import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.lang.Thread.UncaughtExceptionHandler;


public class CrashHandler implements UncaughtExceptionHandler {
    public static final String TAG = "CrashHandler";
    

    private UncaughtExceptionHandler mDefaultHandler;
    
    private static CrashHandler instance;
    
    private Context mContext;

    
    private CrashHandler() {
    }

    
    public static CrashHandler getInstance() {
        if (instance == null) {
            instance = new CrashHandler();
        }
        return instance;
    }

    
    public void init(Context context) {
        mContext = context;
        
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && mDefaultHandler != null) {
            
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            try {
                Thread.sleep(3000);
                
                mDefaultHandler.uncaughtException(thread, ex);
            } catch (Exception e) {
                Log.e(TAG, "error : ", e);
            }
        }
    }

    
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }
        
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                
                if (BuildConfig.DEBUG) {
                    Toast.makeText(mContext, ",,.", Toast.LENGTH_SHORT).show();
                }
                Looper.loop();
            }
        }.start();
        
        sendCrashLog2PM(ex);
        return true;
    }

    
    private void sendCrashLog2PM(Throwable ex) {
        ex.printStackTrace();
    }
}  
