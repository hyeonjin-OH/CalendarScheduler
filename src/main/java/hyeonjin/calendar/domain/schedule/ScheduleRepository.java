package hyeonjin.calendar.domain.schedule;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class ScheduleRepository {

    @PersistenceContext
    private EntityManager em;
    private Long seq = 0L;

    @Transactional
    public Schedule save(Schedule schedule){
        LocalDateTime nowT = LocalDateTime.now();

        schedule.setScdRgdt(nowT);
        schedule.setScdWkno(++seq);
        schedule.setScdFlag("Y");
        em.persist(schedule);
        return schedule;
    }

    @Modifying
    @Transactional
    public Integer updateById(Schedule schedule){
        LocalDateTime nowT = LocalDateTime.now();

        return em.createQuery("update schedulelist s set s.scdTitle=:title, s.scdUpdt=:updt, s.scdFrdt=:frdt, s.scdFrtmchk=:frtmchk, s.scdFrtm=:frtm, s.scdTodt=:todt, s.scdTotmchk=:totmchk, s.scdTotm=:totm, s.scdCnts=:cnts"+
                " where s.scdId=:loginid and s.scdSeqn=:seqn and s.id=:id")
                .setParameter("updt", nowT)
                .setParameter("title", schedule.getScdTitle())
                .setParameter("frdt", schedule.getScdFrdt())
                .setParameter("todt", schedule.getScdTodt())
                .setParameter("frtm", schedule.getScdFrtm())
                .setParameter("totm", schedule.getScdTotm())
                .setParameter("frtmchk", schedule.getScdFrtmchk())
                .setParameter("totmchk", schedule.getScdTotmchk())
                .setParameter("cnts", schedule.getScdCnts())
                .setParameter("loginid", schedule.getScdId())
                .setParameter("seqn", schedule.getScdSeqn())
                .setParameter("id", schedule.getId())
                .executeUpdate();
    }

    @Modifying
    @Transactional
    public Integer deleteById(Schedule schedule){
        LocalDateTime nowT = LocalDateTime.now();

        return em.createQuery("update schedulelist s set s.scdUpdt=:updt, s.scdFlag='N'"+
                        " where s.scdId=:loginid and s.scdSeqn=:seqn and s.id=:id")
                .setParameter("updt", nowT)
                .setParameter("loginid", schedule.getScdId())
                .setParameter("seqn", schedule.getScdSeqn())
                .setParameter("id", schedule.getId())
                .executeUpdate();
    }

    public List<Schedule> findByschSeqn(Long seqn){
        return em.createQuery("select s from schedulelist s where s.scdSeqn = :seqn and s.scdFlag='Y'", Schedule.class)
                .setParameter("seqn", seqn)
                .getResultList();
    }

}
