package hyeonjin.calendar.domain.oauth.google;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GoogleRequest {
    private String redirectUri;
    private String clientId;
    private String clientSecret;
    private String code;
    private String responseType;
    private String grantType;
}
