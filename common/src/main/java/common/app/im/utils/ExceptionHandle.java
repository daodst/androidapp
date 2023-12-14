

package common.app.im.utils;

import com.google.gson.JsonParseException;

import org.json.JSONException;

import java.net.ConnectException;

import common.app.Injection;
import common.app.R;
import common.app.im.exception.Error;
import common.app.im.exception.ExceptionManager;
import common.app.im.exception.RemoteServerException;
import common.app.utils.LogUtil;
import retrofit2.HttpException;


public class ExceptionHandle {

    private static final int UNAUTHORIZED = 401;
    private static final int FORBIDDEN = 403;
    private static final int NOT_FOUND = 404;
    private static final int REQUEST_TIMEOUT = 408;
    private static final int INTERNAL_SERVER_ERROR = 500;
    private static final int BAD_GATEWAY = 502;
    private static final int SERVICE_UNAVAILABLE = 503;
    private static final int GATEWAY_TIMEOUT = 504;
    private static final String TAG = "ExceptionHandle";

    public static ExceptionManager handleException(Throwable e) {
        ExceptionManager ex;
        if (e instanceof HttpException) {
            HttpException httpException = (HttpException) e;
            ex = new ExceptionManager(e, Error.HTTP_ERROR);
            switch (httpException.code()) {
                case UNAUTHORIZED:
                case FORBIDDEN:
                case NOT_FOUND:
                case REQUEST_TIMEOUT:
                case GATEWAY_TIMEOUT:
                case INTERNAL_SERVER_ERROR:
                case BAD_GATEWAY:
                case SERVICE_UNAVAILABLE:
                default:
                    ex.setMessage(Injection.provideContext().getString(R.string.ex_net_error));
                    break;
            }
            return ex;
        } else if (e instanceof RemoteServerException) {
            RemoteServerException resultException = (RemoteServerException) e;
            ex = new ExceptionManager(e,resultException.getCode());
            ex.setMessage(resultException.getMessage());
            return ex;
        } else if (e instanceof JsonParseException
                || e instanceof JSONException) {
            ex = new ExceptionManager(e, Error.PARSE_ERROR);
            LogUtil.d("zzz",e.getMessage());
            ex.setMessage(Injection.provideContext().getString(R.string.ex_parse_error));
            return ex;
        } else if (e instanceof ConnectException) {
            ex = new ExceptionManager(e, Error.NETWORD_ERROR);
            ex.setMessage(Injection.provideContext().getString(R.string.ex_conn_error));
            return ex;
        } else if (e instanceof javax.net.ssl.SSLHandshakeException) {
            ex = new ExceptionManager(e, Error.SSL_ERROR);
            ex.setMessage(Injection.provideContext().getString(R.string.ex_ssl_error));
            return ex;
        } else {
            ex = new ExceptionManager(e, Error.UNKNOWN);
            ex.setMessage(Injection.provideContext().getString(R.string.ex_unknow_error));
            return ex;
        }

    }
}
