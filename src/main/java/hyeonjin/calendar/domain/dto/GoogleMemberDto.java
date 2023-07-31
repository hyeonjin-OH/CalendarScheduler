package hyeonjin.calendar.domain.dto;

import hyeonjin.calendar.domain.member.Member;
import hyeonjin.calendar.domain.oauth.google.GoogleToken;
import lombok.Data;

@Data
public class GoogleMemberDto {

    // google에서 넘겨주는 데이터 키와 동일하게 써야함 혹은 @JSonProperty("key")
    private String id;
    private String email;
    private String name;

    public Member toEntity(GoogleToken googleToken){
        return Member.socialMember()
                .mbrEmail(email)
                .mbrNick(name)
                .mbrAccesstoken(googleToken.getAccess_token())
                .mbrRefreshtoken(googleToken.getRefresh_token())
                .mbrSocialserver("google")
                .build();
    }
}
