

package common.app.base.model.http.exception;

import android.content.Context;
import android.net.ParseException;
import android.text.TextUtils;

import com.google.gson.JsonParseException;
import com.google.gson.stream.MalformedJsonException;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;

import java.io.FileNotFoundException;
import java.net.ConnectException;

import common.app.AppApplication;
import common.app.R;
import retrofit2.HttpException;



public class ExceptionHandle {

    private static final int UNAUTHORIZED = 401;
    private static final int FORBIDDEN = 403;
    private static final int NOT_FOUND = 404;
    private static final int REQUEST_TIMEOUT = 408;
    private static final int INTERNAL_SERVER_ERROR = 500;
    private static final int SERVICE_UNAVAILABLE = 503;

    public static ResponseThrowable handleException(Throwable e) {
        if (e == null) {
            return  new ResponseThrowable(e, "unknown error", ERROR.UNKNOWN, true);
        }
        if (e.getCause() != null) {
            e = e.getCause();
        }
        ResponseThrowable ex;
        Context context = AppApplication.getContext();
        if (e instanceof HttpException) {
            HttpException httpException = (HttpException) e;
            String errorInfo = e.getMessage();
            boolean needPush = true;
            
            switch (httpException.code()) {
                case UNAUTHORIZED:
                    errorInfo = context.getString(R.string.common_3_7_string_5);
                    break;
                case FORBIDDEN:
                    errorInfo = context.getString(R.string.common_3_7_string_6);
                    break;
                case NOT_FOUND:
                    errorInfo = context.getString(R.string.common_3_7_string_7);
                    break;
                case REQUEST_TIMEOUT:
                    errorInfo = context.getString(R.string.common_3_7_string_8);
                    break;
                case INTERNAL_SERVER_ERROR:
                    errorInfo = context.getString(R.string.common_3_7_string_9);
                    break;
                case SERVICE_UNAVAILABLE:
                    errorInfo = context.getString(R.string.common_3_7_string_10);
                    break;
                default:
                    errorInfo = context.getString(R.string.common_3_7_string_11);
                    break;
            }
            ex = new ResponseThrowable(e, errorInfo, ERROR.HTTP_ERROR, needPush);
            return ex;
        } else if (e instanceof JsonParseException
                || e instanceof JSONException
                || e instanceof ParseException || e instanceof MalformedJsonException) {
            
            ex = new ResponseThrowable(e, context.getString(R.string.common_3_7_string_12), ERROR.PARSE_ERROR, true);
            return ex;
        } else if (e instanceof ConnectException) {
            
            ex = new ResponseThrowable(e, context.getString(R.string.common_3_7_string_13), ERROR.NETWORD_ERROR, true);
            return ex;
        } else if (e instanceof javax.net.ssl.SSLException) {
            
            ex = new ResponseThrowable(e, context.getString(R.string.common_3_7_string_14), ERROR.SSL_ERROR, true);
            return ex;
        } else if (e instanceof ConnectTimeoutException) {
            
            ex = new ResponseThrowable(e, context.getString(R.string.common_3_7_string_15), ERROR.TIMEOUT_ERROR, true);
            return ex;
        } else if (e instanceof java.net.SocketTimeoutException) {
            
            ex = new ResponseThrowable(e, context.getString(R.string.common_3_7_string_16), ERROR.TIMEOUT_ERROR, true);
            return ex;
        } else if (e instanceof java.net.UnknownHostException) {
            
            ex = new ResponseThrowable(e, context.getString(R.string.common_3_7_string_17), ERROR.TIMEOUT_ERROR, true);
            return ex;
        } else if (e instanceof FileNotFoundException) {
            
            ex = new ResponseThrowable(e, context.getString(R.string.common_3_7_string_18), ERROR.TIMEOUT_ERROR, true);
            return ex;
        } else if (e instanceof ResponseThrowable) {
            ex = (ResponseThrowable) e;
            String errorMsg = dealServerMsg(context, ex.getErrorInfo());
            ex.setErrorInfo(errorMsg);
            return ex;
        } else {
            String errorMsg = dealServerMsg(context, e.getMessage());
            ex = new ResponseThrowable(e, errorMsg, ERROR.UNKNOWN, true);
            return ex;
        }
    }

    
    private static String dealServerMsg(Context context, String msg) {
        if (null == context || TextUtils.isEmpty(msg)) {
            return msg;
        }
        String realMsgName = "error_" + msg.toLowerCase()
                .replace("'", "")
                .replace("%", "")
                .replace(";", "")
                .replace("(", "")
                .replace(",", "")
                .replace(")", "")
                .replace("-", "")
                .replace(" ", "_");

        int id = context.getResources().getIdentifier(realMsgName, "string", context.getPackageName());
        String realMsg = "";
        if (0 != id) {
            realMsg = context.getString(id);
        } else {
            if (msg.contains("insufficient funds")) {
                realMsg = context.getString(R.string.balance_no_enough);
            } else if (msg.contains("insufficient level to transfer")) {
                int strid = context.getResources().getIdentifier("error_insufficient_level_to_transfer", "string", context.getPackageName());
                realMsg = context.getString(strid);
            }
        }
        if (!TextUtils.isEmpty(realMsg)) {
            return realMsg;
        } else {
            return msg;
        }
    }


    
    class ERROR {

        
        public static final int STATUS_ERROR_0 = 0;


        
        public static final int UNKNOWN = 1000;
        
        public static final int PARSE_ERROR = 1001;
        
        public static final int NETWORD_ERROR = 1002;
        
        public static final int HTTP_ERROR = 1003;

        
        public static final int SSL_ERROR = 1005;

        
        public static final int TIMEOUT_ERROR = 1006;
    }

}

