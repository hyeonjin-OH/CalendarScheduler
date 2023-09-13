package hyeonjin.calendar.domain.exception;

public class LoginFailedException extends CcalendarException{

    public LoginFailedException() {
        super(ErrorCode.LOGIN_FAILED.getMessage());
    }

    public LoginFailedException(String message, Throwable cause) {
        super(ErrorCode.LOGIN_FAILED.getMessage(), cause);
    }

    @Override
    public int getStatus() {
        return ErrorCode.LOGIN_FAILED.getStatus();
    }

    @Override
    public String getErrorCode() {
        return ErrorCode.LOGIN_FAILED.getErrCode();
    }
}
