package hyeonjin.calendar.domain.member;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public class MemberRepository {

    @PersistenceContext
    private  EntityManager em;
    private Long seq = 0L;

    @Transactional(rollbackOn = {SQLException.class})
    public Member save(Member member){
            em.persist(member);

            return member;
    }

    @Modifying
    @Transactional
    public Integer updateInfo(Member member){
        LocalDateTime nowT = LocalDateTime.now();

        if(member.getMbrPwd() != ""){
            return em.createQuery("update memberinfo m set m.mbrUpdt=:updt, m.mbrNick=:nick, m.mbrEmail=:email, m.mbrPwd=:pwd where m.mbrId=:id")
                    .setParameter("updt", nowT)
                    .setParameter("id", member.getMbrId())
                    .setParameter("pwd", member.getMbrPwd())
                    .setParameter("nick", member.getMbrNick())
                    .setParameter("email", member.getMbrEmail())
                    .executeUpdate();
        }
        else{
            return em.createQuery("update memberinfo m set m.mbrUpdt=:updt, m.mbrNick=:nick, m.mbrEmail=:email where m.mbrId=:id")
                    .setParameter("updt", nowT)
                    .setParameter("id", member.getMbrId())
                    .setParameter("nick", member.getMbrNick())
                    .setParameter("email", member.getMbrEmail())
                    .executeUpdate();
        }
    }

    @Modifying
    @Transactional
    public Integer updatePwd(Member member){
        LocalDateTime nowT = LocalDateTime.now();
            return em.createQuery("update memberinfo m set m.mbrUpdt=:updt, m.mbrPwd=:pwd where m.mbrId=:id and m.mbrEmail=:email")
                    .setParameter("updt", nowT)
                    .setParameter("id", member.getMbrId())
                    .setParameter("pwd", member.getMbrPwd())
                    .setParameter("email", member.getMbrEmail())
                    .executeUpdate();
    }

    @Modifying
    @Transactional(rollbackOn = {SQLException.class})
    public Integer updateSeqn(String id, Long seqn){
        LocalDateTime nowT = LocalDateTime.now();

        return em.createQuery("update memberinfo m set m.mbrUpdt=:updt, m.mbrSeqn=:seqn where m.mbrId=:id")
                .setParameter("updt", nowT)
                .setParameter("id", id)
                .setParameter("seqn", seqn)
                .executeUpdate();
    }
    public Member findById(String id){
        return em.find(Member.class, id);
    }

    public Optional<Member> findByMbrId(String loginid){
        return em.createQuery("select c from memberinfo c where c.mbrId = :loginid", Member.class)
                .setParameter("loginid", loginid)
                .getResultList().stream().findAny();
    }

    public Optional<Member> findByMbrIdAndEmail(String loginid, String mbremail){
        return em.createQuery("select c from memberinfo c where c.mbrId = :loginid and c.mbrEmail =:email", Member.class)
                .setParameter("loginid", loginid)
                .setParameter("email", mbremail)
                .getResultList().stream().findAny();
    }

    public Optional<Member> findByMbrEmail(String mbremail){
        return em.createQuery("select c from memberinfo c where c.mbrEmail =:email", Member.class)
                .setParameter("email", mbremail)
                .getResultList().stream().findAny();
    }

    public Optional<Member> findSocialMember(String mbremail, String social, String mbrid){
        return em.createQuery("select c from memberinfo c where c.mbrEmail =:email and c.mbrId = :id and c.mbrSocialserver=:social", Member.class)
                .setParameter("email", mbremail)
                .setParameter("social", social)
                .setParameter("id", mbrid)
                .getResultList().stream().findAny();
    }

    public Optional<Member> findSeqn(String loginid){
        return em.createQuery("select c from memberinfo c where c.mbrId = :loginid", Member.class)
                .setParameter("loginid", loginid)
                .getResultList().stream().findAny();
    }

    public Long findMaxMember(){
        return em.createQuery("select count(c) from memberinfo c", Long.class)
                .getSingleResult();
    }

    @Modifying
    @Transactional(rollbackOn = {SQLException.class})
    public Integer updateRefreshToken(String id, String email, String refreshToken){
        LocalDateTime nowT = LocalDateTime.now();

        return em.createQuery("update memberinfo m set m.mbrUpdt=:updt, m.mbrRefreshtoken=:refreshtoken where m.mbrId=:id and m.mbrEmail=:email")
                .setParameter("updt", nowT)
                .setParameter("id", id)
                .setParameter("email", email)
                .setParameter("refreshToken", refreshToken)
                .executeUpdate();

    }

    public Optional<Member> findRefreshToken(String id, String email){
        return em.createQuery("select m from memberinfo m where m.mbrId=:id and m.mbrEmail=:email", Member.class)
                .setParameter("id", id)
                .setParameter("email", email)
                .getResultList().stream().findAny();
    }

    public Optional<Member> findByRefreshToken(String refreshToken) {
        return em.createQuery("select m from memberinfo m where m.mbrRefreshtoken=:refresh", Member.class)
                .setParameter("refresh", refreshToken)
                .getResultList().stream().findAny();
    }

    public String findNickName(String id){
        return em.createQuery("select m.mbrNick from memberinfo m where m.mbrId =:id", String.class)
                .setParameter("id", id)
                .getSingleResult();
    }


}
