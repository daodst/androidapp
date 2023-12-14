

package common.app.im.exception;


public class ExceptionManager extends Exception {

    private int mCode;

    private String mMessage;

    @Override
    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        mMessage = message;
    }

    public int getCode() {
        return mCode;
    }

    public ExceptionManager(Throwable cause, int code) {
        super(cause);
        this.mCode = code;
    }
}
