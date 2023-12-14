

package common.app.utils;


import com.orhanobut.logger.Logger;

import common.app.BuildConfig;


public class LogUtil {

    public static final int VERBOSE = 1;
    public static final int DEBUG = 2;
    public static final int INFO = 3;
    public static final int WARN = 4;
    public static final int ERROR = 5;
    public static final int NONO = 6;
    public static final int LEVEL = VERBOSE;
    public static final boolean logable = BuildConfig.DEBUG;
    public static void v(String tag, String msg) {
        if (LEVEL <= VERBOSE&&logable) {
            Logger.t(tag).v(msg);
        }
    }

    public static void d(String tag, String msg) {
        if (LEVEL <= DEBUG&&logable) {
            Logger.t(tag).d(msg);
        }
    }


    public static void i(String tag, String msg) {
        if (LEVEL <= INFO&&logable) {
            Logger.t(tag).i(msg);
        }
    }


    public static void w(String tag, String msg) {
        if (LEVEL <= WARN&&logable) {
            Logger.t(tag).w(msg);
        }
    }


    public static void e(String tag, String msg) {
        if (LEVEL <= ERROR&&logable) {
            Logger.t(tag).e(msg);
        }
    }

    public static void json(String tag, String msg) {
        if (LEVEL <= DEBUG&&logable) {
            Logger.t(tag).json(msg);
        }
    }

    public static void xml(String tag, String msg) {
        if (LEVEL <= DEBUG&&logable) {
            Logger.t(tag).xml(msg);
        }
    }
}
