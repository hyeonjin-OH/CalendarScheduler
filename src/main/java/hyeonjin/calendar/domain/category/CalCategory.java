package hyeonjin.calendar.domain.category;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity(name="categoryinfo")    //createquery에 쓰일 이름
@Table(name="categoryinfo")
@Builder
public class CalCategory {

    public CalCategory(){

    }

    /**
     * ctgrCode : 카테고리 코드(ct1. ct2 ..)
     * ctgrSeqn : 카테고리 시퀀스(member테이블 외래키)
     * ctgrId : 카테고리 사용자 id
     * ctgrColr : 해당 카테고리 내 선택 칼라
     * ctgrFlag : 카테고리 상태여부/ 사용Y 수정(히스토리남기는용)S 삭제D 등
     */

    private LocalDateTime ctgrRgdt;
    private LocalDateTime ctgrUpdt;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ctgrCode;
    private Long ctgrSeqn;
    private String ctgrId;
    private String ctgrCrdt;
    private String ctgrColr;
    private Character ctgrFlag;

    //@OneToMany(mappedBy="ctgr")   <->Member에 선언된 이름
    //2private List<Member> members = new ArrayList<>();

}
