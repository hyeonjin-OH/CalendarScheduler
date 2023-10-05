package hyeonjin.calendar.domain.member;

import hyeonjin.calendar.domain.dto.MemberDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@Data
//@Getter
@Entity(name="memberinfo")
@Table(name="memberinfo")
@NoArgsConstructor
@Builder
@DynamicUpdate
@AllArgsConstructor
public class Member {

    @Builder(builderMethodName = "loginMember", builderClassName = "loginMember")
    public Member(Long id, String mbrId, String mbrNick, String mbrEmail, Long mbrSeqn,
                 String mbrCtgr, String mbrColr) {
        this.id = id;
        this.mbrId = mbrId;
        this.mbrNick = mbrNick;
        this.mbrEmail = mbrEmail;
        this.mbrSeqn = mbrSeqn;
        this.mbrCtgr = mbrCtgr;
        this.mbrColr = mbrColr;
    }

    @Builder(builderMethodName = "socialMember", builderClassName = "socialMember")
    public Member(Long id, String mbrId, String mbrNick, String mbrEmail, Long mbrSeqn,
                  String mbrAccesstoken, String mbrRefreshtoken, String mbrSocialserver, String mbrCtgr, String mbrColr) {
        this.id = id;
        this.mbrId = mbrId;
        this.mbrNick = mbrNick;
        this.mbrEmail = mbrEmail;
        this.mbrSeqn = mbrSeqn;
        this.mbrAccesstoken = mbrAccesstoken;
        this.mbrRefreshtoken = mbrRefreshtoken;
        this.mbrSocialserver = mbrSocialserver;
        this.mbrCtgr = "personal";
        this.mbrColr = "blue";
    }


    public void setMbrUpdt(LocalDateTime mbrUpdt) {
        this.mbrUpdt = mbrUpdt;
    }

    public MemberDTO ToDto(){
        return MemberDTO.allBuild()
                .member(this)
                .build();
    }

    /** <summary>
     * 회원정보
     * 가입시 필요 정보 및 스케쥴 로드 시 필요 정보
     * id : site 고유 id
     * memberId : 사용자 id
     * memberNick : 사용자 닉네임
     * memberPwd : 사용자 비밀번호
     * memberTeln : 사용자 휴대폰번호
     * memberEmail : 사용자 이메일
     * 아래로는 타 테이블 정보 - 안쓸 수도 있긴함
     * memberCtgr : default 캘린더 카테고리(개인/가족/업무 등)
     * memberCgSq : 카테고리 일정생성 시퀀스 - 누구와 어떤 카테고리를 공유하는지 (schedule table)
     * memberColr : 카테고리별 일정색깔
    </summary>
     */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)// ,generator = "memberId_Seq") seq cnt table
    private Long id;

    private LocalDateTime mbrRgdt;
    private LocalDateTime mbrUpdt;

    @NotEmpty
    private String mbrId;
    private String mbrNick;
    private String mbrPwd;
    @NotEmpty
    private String mbrEmail;
    private Long mbrSeqn;

    //OAuth 관련 정보 저장 칼럼
    private String mbrAccesstoken;
    private String mbrRefreshtoken;
    private String mbrSocialserver;

    @Transient
    private String mbrCtgr;
    @Transient
    private String mbrColr;

    private String mbrRole;

}
