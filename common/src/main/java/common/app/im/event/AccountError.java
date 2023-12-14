

package common.app.im.event;


public class AccountError {

    public String title;
    public String message;
    public int errorCode;


    
    public static final int ERROR_CODE_INVALID_ACCOUNT = 10001;
    
    public static final int ERROR_CODE_LOGIN_OTHER_DEVICE = 10002;

    
    public static final int ERROR_CODE_HOST_CLOSED = 0x1254;

    
    public static final int ERROR_CODE_QUIT_APP = 0x1255;

    public AccountError(String title, String message, int errorCode) {
        this.title = title;
        this.message = message;
        this.errorCode = errorCode;
    }

    @Override
    public String toString() {
        return "AccountError{" +
                "title='" + title + '\'' +
                ", message='" + message + '\'' +
                ", errorCode=" + errorCode +
                '}';
    }
}
