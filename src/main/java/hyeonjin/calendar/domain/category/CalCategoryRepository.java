package hyeonjin.calendar.domain.category;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class CalCategoryRepository{
    @PersistenceContext
    private EntityManager em;
    public CalCategory save(CalCategory calCategory){

        em.persist(calCategory);
        return calCategory;
    }

    //단독으로 카테고리 추가해야할 경우
    @Transactional
    public CalCategory saveCategory(CalCategory calCategory){

        em.persist(calCategory);
        return calCategory;
    }

    public Optional<CalCategory> findByMbrId(String loginid, Integer idx){
        return em.createQuery("select c from categoryinfo c where c.ctgrId = :loginid and c.ctgrFlag='Y' and c.ctgrIdx=:idx", CalCategory.class)
                .setParameter("loginid", loginid)
                .setParameter("idx", idx)
                .getResultList().stream().findAny();
    }

}
