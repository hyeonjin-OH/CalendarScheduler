package hyeonjin.calendar.web.calendar;

import hyeonjin.calendar.domain.category.CalCategory;
import hyeonjin.calendar.domain.category.CategoryRepository;
import hyeonjin.calendar.domain.member.Member;
import hyeonjin.calendar.domain.member.MembersRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

@Service
@RequiredArgsConstructor
public class CalendarService {

    private final MembersRepository memberRepository;
    private final CategoryRepository categoryRepository;

    @Transactional(rollbackOn = {SQLException.class})
    public void AddPost(CalCategory category, Member member){

        try {
            categoryRepository.save(category);
            memberRepository.save(member);
        }
        catch (Exception e){
            throw e;
        }
    }

}
