

package common.app.crash;

import android.os.Handler;
import android.os.Looper;



public class CrashManager {
    private static ExceptionHandler cExceptionHandler;
    private static Thread.UncaughtExceptionHandler cUncaughtExceptionHandler;
    private static boolean isInstalled = false;

    public interface ExceptionHandler {
        void handlerException(Thread thread, Throwable throwable);
    }

    public static synchronized void install(ExceptionHandler exceptionHandler) {
        if (isInstalled) {
            return;
        }
        isInstalled = true;
        cExceptionHandler = exceptionHandler;

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {

                while (true) {
                    try {
                        Looper.loop();
                    } catch (Throwable e) {
                        if (e instanceof ExitException) {
                            return;
                        }
                        if (cExceptionHandler != null) {
                            cExceptionHandler.handlerException(Looper.getMainLooper().getThread(), e);
                        }
                    }
                }
            }
        });

        cUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                if (cExceptionHandler != null) {
                    cExceptionHandler.handlerException(t, e);
                }
            }
        });

    }

    public static synchronized void uninstall() {
        if (!isInstalled) {
            return;
        }
        isInstalled = false;
        cExceptionHandler = null;
        
        Thread.setDefaultUncaughtExceptionHandler(cUncaughtExceptionHandler);
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                throw new ExitException("Exit");
            }
        });

    }
}
