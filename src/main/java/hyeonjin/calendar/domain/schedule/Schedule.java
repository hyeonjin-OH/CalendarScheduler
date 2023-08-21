package hyeonjin.calendar.domain.schedule;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@Data
@Entity(name ="schedulelist")
@Table(name="schedulelist")
@Builder
@DynamicUpdate
@AllArgsConstructor
public class Schedule {

    public Schedule(){

    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime scdRgdt;
    private LocalDateTime scdUpdt;

    private Long scdSeqn;
    private String scdId;
    private String scdTitle;
    private String scdFrdt;
    private Boolean scdFrtmchk;
    private String scdFrtm;
    private String scdTodt;
    private Boolean scdTotmchk;
    private String scdTotm;
    private Long scdWkno;
    private String scdCnts;

    private String scdColr;

    private String scdFlag;
}
