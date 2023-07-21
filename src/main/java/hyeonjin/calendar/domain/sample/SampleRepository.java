package hyeonjin.calendar.domain.sample;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SampleRepository extends JpaRepository<Sample, Long> {
    Sample findAllById(Long id);
    //Optional<Sample>findByComment(String cmt);

    Optional<Sample>findByAddr(String addr);

    Optional<Sample>findByIdAndComment(Long id, String cmt);


    @Query("select s from sample s where s.comment = :cmt and s.flag='Y'")
    Optional<Sample>findByComment(@Param("cmt") String comment);

}
