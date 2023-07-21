package hyeonjin.calendar.domain.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<CalCategory, Long> {

    Optional<CalCategory> findByCtgrIdAndCtgrSeqn(String id, Long seqn);

    @Query("select c from CalCategory c where c.ctgrSeqn =:seqn and c.ctgrFlag='Y'")
    //@Query(nativeQuery = true, value="select * from categoryinfo where ctgrSeqn =:seqn and ctgrFlag='Y'")
    List<CalCategory> findByCtgrSeqn(Long seqn);




}
