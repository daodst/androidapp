package com.benny.openlauncher;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.view.WindowManager;

import com.benny.openlauncher.util.Tool;

import net.gsantner.opoc.preference.OtherSpUtils;

public class AppObject{
    private Context _context;
    @SuppressLint("StaticFieldLeak")
    private static AppObject _instance;

    public static AppObject get() {
        return _instance;
    }

    public void init(Application application){
        _instance = this;
        _context = application.getApplicationContext();
        OtherSpUtils.init(application);
        Tool.init(application);
    }

    public Context getApplicationContext(){
        return _context;
    }

    private final WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();

    public WindowManager.LayoutParams getMywmParams() {
        return wmParams;
    }
}
