package hyeonjin.calendar.domain.oauth.google;

import lombok.Data;

@Data
public class GoogleToken {
    private String access_token;
    private String expires_in;
    private String refresh_token;
    private String scope;
    private String token_type;   // 반환된 토큰 유형(Bearer 고정)
    private String id_token;
}
