package hyeonjin.calendar.domain.category;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<CalCategory, Long> {


    Optional<CalCategory> findByCtgrIdAndCtgrSeqn(String id, Long seqn);

    @Query("select c from categoryinfo c where c.ctgrSeqn =:seqn and c.ctgrFlag='Y'")
    //@Query(nativeQuery = true, value="select * from categoryinfo where ctgrSeqn =:seqn and ctgrFlag='Y'")
    List<CalCategory> findByCtgrSeqn(@Param("seqn") Long seqn);

    @Query("select c from categoryinfo c where c.ctgrId =:id and c.ctgrFlag='Y' order by ctgrRgdt desc")
    List<CalCategory> findByCtgrId(@Param("id")String id);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value="update categoryinfo c set c.ctgr_flag='N', c.ctgr_updt=:updt where c.ctgr_id=:id and c.ctgr_seqn=:seqn")
    Integer deleteByCtgrSeqn(@Param("id")String id, @Param("seqn")Long seqn, @Param("updt")LocalDateTime updt);


}
