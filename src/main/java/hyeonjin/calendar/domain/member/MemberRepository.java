package hyeonjin.calendar.domain.member;

import hyeonjin.calendar.domain.category.CalCategory;
import hyeonjin.calendar.domain.category.CalCategoryRepository;
import hyeonjin.calendar.web.category.categoryController;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Repository
public class MemberRepository {

    @PersistenceContext
    private  EntityManager em;

    @Autowired
    private CalCategoryRepository calRepository = new CalCategoryRepository();

    private final categoryController ctgrController;
    private Long seq = 0L;

    public MemberRepository(categoryController ctgrController) {
        this.ctgrController = ctgrController;
    }

    @Transactional(rollbackOn = {SQLException.class})
    public Member save(Member member){
        seq = findMaxMember();
        //try catch작업
            CalCategory calCategory = new CalCategory();
            LocalDateTime nowT = LocalDateTime.now();
            if(member.getMbrSeqn()== null){
                member.setMbrSeqn(Long.parseLong(DateTimeFormatter.ofPattern("HHmmss").format(nowT) + (++seq).toString()));
            }
            member.setMbrRgdt(nowT);
            em.persist(member);
            ctgrController.save(member);
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

    public Optional<Member> findByMbrPwd(String loginid, String pwd){
        return em.createQuery("select c from member c where c.mbrId = :loginid and c.mbrPwd = :pwd", Member.class)
                .setParameter("loginid", loginid)
                .setParameter("pwd", pwd)
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

    public Optional<Member> findSocialMember(String mbremail){
        return em.createQuery("select c from memberinfo c where c.mbrEmail =:email and c.mbrId LIKE :id", Member.class)
                .setParameter("email", mbremail)
                .setParameter("id", "social%")
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
    public List<Member> findAll(){

        return null;
    }

}
