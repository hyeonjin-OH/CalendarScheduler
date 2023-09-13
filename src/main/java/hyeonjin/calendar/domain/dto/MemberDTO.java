package hyeonjin.calendar.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberDTO {
    private Long id;
    private String mbrId;
    private String mbrPwd;
    private String mbrEmail;
    private Long mbrSeqn;
    private String mbrNick;
}
