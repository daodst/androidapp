

package common.app;

import android.app.Application;
import android.content.Context;

public class Injection {

    public static Application mApplication;

    public static Context provideContext() {
        return mApplication;
    }
}
