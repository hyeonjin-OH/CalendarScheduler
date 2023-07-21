package hyeonjin.calendar.domain.sample;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity(name="sample")
public class Sample {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String comment;
    private String addr;
    private Long idx;
    private char flag;
}
