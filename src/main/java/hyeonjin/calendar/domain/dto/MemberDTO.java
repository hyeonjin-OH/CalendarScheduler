package hyeonjin.calendar.domain.dto;

import hyeonjin.calendar.domain.member.Member;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class MemberDTO {
    private Long id;
    private LocalDateTime mbrRgdt;
    private LocalDateTime mbrUpdt;
    private String mbrId;
    private String mbrNick;
    private String mbrPwd;
    private String mbrEmail;
    private Long mbrSeqn;
    private String mbrCtgr;
    private String mbrColr;

    private String mbrAccesstoken;
    private String mbrRefreshtoken;
    private String mbrSocialserver;

    public void UpdatePwd(String mbrId, String mbrEmail, String mbrPwd, LocalDateTime mbrUpdt){
        this.mbrId = mbrId;
        this.mbrEmail = mbrEmail;
        this.mbrPwd = mbrPwd;
        this.mbrUpdt = mbrUpdt;
    }

    public String encryptPwd(String mbrPwd){
        this.mbrPwd = mbrPwd;
        return this.mbrPwd;
    }

    public Member ToEntity(){
        return Member.builder()
                .id(id)
                .mbrRgdt(mbrRgdt)
                .mbrUpdt(mbrUpdt)
                .mbrId(mbrId)
                .mbrPwd(mbrPwd)
                .mbrEmail(mbrEmail)
                .mbrNick(mbrNick)
                .mbrSeqn(mbrSeqn)
                .mbrAccesstoken(mbrAccesstoken)
                .mbrSocialserver(mbrSocialserver)
                .mbrRefreshtoken(mbrRefreshtoken)
                .mbrColr(mbrColr)
                .mbrCtgr(mbrCtgr)
                .build();
    }

    @Builder(builderMethodName = "allBuild", builderClassName = "allBuild")
    public MemberDTO(Member member){
        this.id = member.getId();
        this.mbrRgdt = member.getMbrRgdt();
        this.mbrUpdt = member.getMbrUpdt();
        this.mbrId = member.getMbrId();
        this.mbrPwd = member.getMbrPwd();
        this.mbrNick = member.getMbrNick();
        this.mbrEmail = member.getMbrEmail();
        this.mbrSeqn = member.getMbrSeqn();
        this.mbrCtgr = member.getMbrCtgr();
        this.mbrColr = member.getMbrColr();
        this.mbrAccesstoken = member.getMbrAccesstoken();
        this.mbrRefreshtoken = member.getMbrRefreshtoken();
        this.mbrSocialserver = member.getMbrSocialserver();
    }

    @Builder(builderMethodName = "allBuilder", builderClassName="allBuilder", toBuilder = true)
    public MemberDTO(Long id, LocalDateTime mbrRgdt, LocalDateTime mbrUpdt, String mbrId, String mbrPwd,
                     String mbrNick, String mbrEmail, Long mbrSeqn, String mbrCtgr, String mbrColr,
                     String mbrAccesstoken, String mbrRefreshtoken, String mbrSocialserver){
        this.id = id;
        this.mbrRgdt = mbrRgdt;
        this.mbrUpdt = mbrUpdt;
        this.mbrId = mbrId;
        this.mbrPwd = mbrPwd;
        this.mbrNick = mbrNick;
        this.mbrEmail = mbrEmail;
        this.mbrSeqn = mbrSeqn;
        this.mbrCtgr = mbrCtgr;
        this.mbrColr = mbrColr;
        this.mbrAccesstoken = mbrAccesstoken;
        this.mbrRefreshtoken = mbrRefreshtoken;
        this.mbrSocialserver = mbrSocialserver;
    }

    @Builder(builderMethodName = "loginMember", builderClassName = "loginMember")
    public MemberDTO(Long id, String mbrId, String mbrNick, String mbrEmail, Long mbrSeqn,
                  String mbrCtgr, String mbrColr) {
        this.id = id;
        this.mbrId = mbrId;
        this.mbrNick = mbrNick;
        this.mbrEmail = mbrEmail;
        this.mbrSeqn = mbrSeqn;
        this.mbrCtgr = mbrCtgr;
        this.mbrColr = mbrColr;
    }

}
