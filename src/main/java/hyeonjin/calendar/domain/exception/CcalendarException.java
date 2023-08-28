package hyeonjin.calendar.domain.exception;

import lombok.Getter;

@Getter
public abstract class CcalendarException extends Exception{

    public CcalendarException(String message, Throwable cause) {
        super(message, cause);
    }

    public CcalendarException(String message) {
        super(message);
    }

    public abstract String getErrorCode();

    public abstract int getStatus();
}
