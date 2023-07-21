package hyeonjin.calendar.domain.member;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity(name="memberinfo")
@Table(name="memberinfo")
public class Member {

    public Member(){

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
                   // ,generator = "memberId_Seq") seq cnt table
    private Long id;

    private LocalDateTime mbrRgdt;
    private LocalDateTime mbrUpdt;

    @NotEmpty
    private String mbrId;
    private String mbrNick;
    private String mbrPwd;
    private String mbrEmail;
    private String mbrSeqn;


    @Transient
    private String mbrCtgr;
    @Transient
    private String mbrColr;


}
