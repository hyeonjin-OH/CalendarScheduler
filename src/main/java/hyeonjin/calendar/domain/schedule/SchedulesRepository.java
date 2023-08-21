package hyeonjin.calendar.domain.schedule;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SchedulesRepository extends JpaRepository<Schedule, Long> {

    public Long countByScdSeqn(@Param("seqn")Long seqn);

    public List<Schedule> findByScdSeqnAndScdFlag(Long seqn, String flag);

    @Query("select s from schedulelist s where s.scdSeqn = :seqn and s.scdWkno = :wkno and s.scdFlag='Y'")
    public Schedule findByScdWknoAndScdSeqn(@Param("wkno")Long wkno, @Param("seqn")Long seqn);


    @Query("select s from schedulelist s where s.scdSeqn = :seqn and s.scdCnts LIKE :cnts or s.scdTitle LIKE :cnts and s.scdFlag='Y' order by s.scdFrdt desc")
    public List<Schedule> findByScdSeqnAndScdCnts(@Param("seqn")Long seqn, @Param("cnts")String cnts);
}
