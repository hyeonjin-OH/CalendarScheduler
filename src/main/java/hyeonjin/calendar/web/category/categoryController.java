package hyeonjin.calendar.web.category;


import hyeonjin.calendar.domain.category.CalCategory;
import hyeonjin.calendar.domain.category.CategoryRepository;
import hyeonjin.calendar.domain.member.Member;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@RequestMapping("/CalendarCategory")
@Controller
public class categoryController {
    private final CategoryRepository categoryRepository;

    public categoryController(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public String save(Member member){
        CalCategory calCategory = new CalCategory();
        calCategory.setCtgrCode(member.getMbrCtgr());
        calCategory.setCtgrId(member.getMbrId());
        calCategory.setCtgrSeqn(Long.parseLong(member.getMbrSeqn()));
        calCategory.setCtgrCrdt(DateTimeFormatter.ofPattern("yyyyMMdd").format(member.getMbrRgdt()));
        calCategory.setCtgrColr(member.getMbrColr());
        calCategory.setCtgrRgdt(member.getMbrRgdt());
        calCategory.setCtgrFlag('Y');

        categoryRepository.save(calCategory);
        return "";
    }

    @RequestMapping(value = "/addCtgr" )
    public String register(@ModelAttribute("member") Member member){

        Long ctgrSeqn = Long.parseLong(member.getMbrSeqn());
        Optional<CalCategory> ctgr = categoryRepository.findByCtgrSeqn(ctgrSeqn).stream().findFirst();
        String code = ctgr.get().getCtgrCode();

        CalCategory calCategory = new CalCategory();
        LocalDateTime nowT = LocalDateTime.now();
        calCategory.setCtgrRgdt(nowT);
        calCategory.setCtgrId(member.getMbrId());
        calCategory.setCtgrCode(code);
        calCategory.setCtgrSeqn(Long.parseLong(member.getMbrSeqn()));
        calCategory.setCtgrCrdt(DateTimeFormatter.ofPattern("yyyyMMdd").format(nowT));
        calCategory.setCtgrColr(member.getMbrColr());
        calCategory.setCtgrFlag('Y');

        categoryRepository.save(calCategory);

        return "";
    }

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
