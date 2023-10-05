package hyeonjin.calendar.domain.exception;

import lombok.Getter;

//Ccalendar 전체 익셉션관
@Getter
public class CcalendarException extends RuntimeException{

    private final ErrorCode errorCode;
    public CcalendarException(ErrorCode e) {
        super(e.getMessage());
        this.errorCode = e;
    }

    public int getStatus(ErrorCode e) {
        return e.getStatus();
    }

    public String getErrorCode(ErrorCode e) {
        return e.getErrCode();
    }}
