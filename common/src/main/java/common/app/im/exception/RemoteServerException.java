

package common.app.im.exception;


public class RemoteServerException extends Exception {
    private int mCode;

    public int getCode() {
        return mCode;
    }

    public RemoteServerException(String message) {
        super(message);
    }

    public RemoteServerException(String message, int code) {
        super(message);
        this.mCode = code;
    }
}
