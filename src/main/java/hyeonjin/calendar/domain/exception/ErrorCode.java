package hyeonjin.calendar.domain.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    AUTHENTICATION_FAILED(401, "AUTH-401", "로그인 인증에 실패하였습니다."),
    LOGIN_FAILED(402, "LOGINFAILED-402", "로그인에 실패하였습니다."),
    MEMBER_NOT_FOUND(404,"MEMBER-404", "존재하지 않는 회원잉ㅂ니다."),
    UNDEFINED(404, "UNDEFINED-404", "알 수 없는 에러입니다.")
    ;

    private int status;
    private String errCode;
    private String message;

    ErrorCode(int status, String errCode, String message) {
        this.status = status;
        this.errCode = errCode;
        this.message = message;
    }
}
