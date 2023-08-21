package hyeonjin.calendar.web.category;

import hyeonjin.calendar.domain.category.CalCategory;
import hyeonjin.calendar.domain.category.CategoryRepository;
import hyeonjin.calendar.domain.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;


@Controller
@RequestMapping("/CalendarCategory")
@RequiredArgsConstructor
public class categoryController {

    @Autowired
    private final CategoryRepository categoryRepository;


    public void save(Member member){
        CalCategory calCategory = CalCategory.builder()
                 .ctgrRgdt(member.getMbrRgdt())
                .ctgrId(member.getMbrId())
                .ctgrCode(member.getMbrCtgr())
                .ctgrSeqn(member.getMbrSeqn())
                .ctgrColr(member.getMbrColr())
                .ctgrCrdt(DateTimeFormatter.ofPattern("yyyyMMdd").format(member.getMbrRgdt()))
                .ctgrFlag('Y')
                .build();

        categoryRepository.save(calCategory);
    }
/*
    @RequestMapping(value = "/addCtgr" )
    public String register(@ModelAttribute("member") Member member){

        Long ctgrSeqn =member.getMbrSeqn();
        Optional<CalCategory> ctgr = categoryRepository.findByCtgrSeqn(ctgrSeqn).stream().findFirst();
        String code = ctgr.get().getCtgrCode();
        LocalDateTime nowT = LocalDateTime.now();

        CalCategory.builder()
                .ctgrRgdt(nowT)
                .ctgrId(member.getMbrId())
                .ctgrCode(code)
                .ctgrSeqn(member.getMbrSeqn())
                .ctgrColr(member.getMbrColr())
                .ctgrCrdt(DateTimeFormatter.ofPattern("yyyyMMdd").format(nowT))
                .ctgrFlag('Y')
                .build();

        categoryRepository.save(calCategory);

        return "";
    }

 */

    //캘린더 카테고리 추가 시, 선택된 색상 제외 나머지 색상 리턴
    @RequestMapping(value = "/findCtgr" )
    @ResponseBody
    public String findInfo(@ModelAttribute("ctgrseqn")String seqn){
        Long ctgrSeqn = Long.parseLong(seqn);
        List<CalCategory> ctgr = categoryRepository.findByCtgrSeqn(ctgrSeqn);
        String res ="";

        if(ctgr.equals(Optional.empty()))
        {
            return "";
        }
        else{
            for(int i=0; i<ctgr.size(); i++){
                res += ctgr.get(i).getCtgrColr()+",";
            }
            res = res.substring(0, res.length()-1);
            return res;
        }
    }


}
