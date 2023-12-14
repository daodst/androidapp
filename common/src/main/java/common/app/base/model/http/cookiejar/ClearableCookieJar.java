

package common.app.base.model.http.cookiejar;

import okhttp3.CookieJar;


public interface ClearableCookieJar extends CookieJar {

    
    void clearSession();

    
    void clear();
}
