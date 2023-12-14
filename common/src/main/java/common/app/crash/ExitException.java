

package common.app.crash;



public class ExitException extends RuntimeException {
    public ExitException(String message) {
        super(message);
    }
}
