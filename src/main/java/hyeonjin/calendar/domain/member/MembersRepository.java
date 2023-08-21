package hyeonjin.calendar.domain.member;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MembersRepository extends JpaRepository<Member, Long> {

    public Optional<Member> findByMbrId(String loginid);

    public Optional<Member> findByMbrIdAndMbrEmail(String loginid, String mbrmail);

    public Optional<Member> findByMbrEmail(String mbremail);

    @Query("select m.mbrSeqn from memberinfo m where m.mbrId = :id")
    public Long findSeqn(@Param("id") String loginid);

    @Query("select m.mbrNick from memberinfo m where m.mbrId = :id")
    public String findNickName(@Param("id") String loginid);
    public Long countBy();


}
