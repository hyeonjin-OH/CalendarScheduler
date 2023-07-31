package hyeonjin.calendar.web.calendar;

import hyeonjin.calendar.domain.category.CalCategory;
import hyeonjin.calendar.domain.category.CategoryRepository;
import hyeonjin.calendar.domain.member.Member;
import hyeonjin.calendar.domain.member.MemberRepository;
import hyeonjin.calendar.domain.schedule.Schedule;
import hyeonjin.calendar.domain.schedule.ScheduleRepository;
import hyeonjin.calendar.web.SessionConst;
import hyeonjin.calendar.web.session.SessionManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Controller
@Slf4j
@RequestMapping("/Calendar")
@RequiredArgsConstructor
public class MCalendarController {

    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;
    private final ScheduleRepository scheduleRepository;
    private final SessionManager sessionManager;

    @GetMapping
    public String scheduleInfo(@ModelAttribute("schedule") Schedule scd,
                               HttpServletRequest request, Model model) {

        //세션이 있으면 있는 세션 반환, 없으면 신규 세션 생성
        HttpSession session = request.getSession();
        Member m = (Member)session.getAttribute(SessionConst.LOGIN_MEMBER);

        Optional<CalCategory> mbrCtgr = categoryRepository.findByCtgrIdAndCtgrSeqn(m.getMbrId(),m.getMbrSeqn());
        CalCategory ctgrInfo = mbrCtgr.get();

        Long ctgSeqn = ctgrInfo.getCtgrSeqn();
        String ctgColr = ctgrInfo.getCtgrColr();

        List<Schedule> schedule = scheduleRepository.findByschSeqn(ctgSeqn);

        model.addAttribute("member", m);

        model.addAttribute("schedules",schedule);

        scd.setScdSeqn(ctgSeqn);
        scd.setScdId(m.getMbrId());
        scd.setScdColr(ctgColr);

        //return "view/calendar/monthCalendar";
        return "view/calendar/fullCalendar";
    }

    @RequestMapping(value="/schedules", method = RequestMethod.GET)
    @ResponseBody
    public String calendarInfo(HttpServletRequest request) {

        //세션이 있으면 있는 세션 반환, 없으면 신규 세션 생성
        HttpSession session = request.getSession();
        Member m = (Member)session.getAttribute(SessionConst.LOGIN_MEMBER);

        String mbrId = m.getMbrId();
        Optional<Member> mbrSeqn = memberRepository.findSeqn(mbrId);

        Optional<CalCategory> mbrCtgr = categoryRepository.findByCtgrIdAndCtgrSeqn(mbrId,mbrSeqn.get().getMbrSeqn());

        CalCategory ctgrInfo = mbrCtgr.get();

        Long ctgSeqn = ctgrInfo.getCtgrSeqn();
        String ctgColr = ctgrInfo.getCtgrColr();

        List<Schedule> schedule = scheduleRepository.findByschSeqn(ctgSeqn);

        HashMap<String, Object> hash = new HashMap<>();

        JSONObject jsonObj = new JSONObject();
        JSONArray jsonArr = new JSONArray();

        for (int i = 0; i < schedule.size(); i++) {
            String  starttime = schedule.get(i).getScdFrdt() +"T" + schedule.get(i).getScdFrtm();
            String  endtime = schedule.get(i).getScdTodt() +"T" + schedule.get(i).getScdTotm();

            hash.put("title", schedule.get(i).getScdTitle());
            hash.put("start", starttime);
            hash.put("end", endtime);
            hash.put("color", schedule.get(i).getScdColr());
            hash.put("description", schedule.get(i).getScdCnts());
            hash.put("allDay",false);
            hash.put("display","block");
            HashMap<String, Object> temp = new HashMap<>();
            temp.put("scdId", schedule.get(i).getScdId());
            temp.put("id", schedule.get(i).getId());
            temp.put("scdWkno", schedule.get(i).getScdWkno());
            hash.put("extendedProps", temp);
            jsonObj = new JSONObject(hash);
            jsonArr.put(jsonObj);
        }

        return jsonArr.toString();
    }

    @GetMapping("/add")
    public String calendarAdd(Model model, HttpServletRequest request){
        //세션이 있으면 있는 세션 반환, 없으면 신규 세션 생성
        HttpSession session = request.getSession();
        Member m = (Member)session.getAttribute(SessionConst.LOGIN_MEMBER);

        model.addAttribute("member",m);

        return "/view/calendar/addCalendar";
    }
    @PostMapping("/add")
    public String calendarAddPost(@ModelAttribute("member") Member member, Model model, HttpServletRequest request){

        Long ctgrSeqn = 0L;
        String code = "";
        CalCategory calCategory = new CalCategory();
        LocalDateTime nowT = LocalDateTime.now();
        Long seq = 0L;

        if(member.getMbrSeqn() != null){
            ctgrSeqn=member.getMbrSeqn();
            Optional<CalCategory> ctgr = categoryRepository.findByCtgrSeqn(ctgrSeqn).stream().findFirst();
            code = ctgr.get().getCtgrCode();
        }
        else{
            ctgrSeqn = Long.parseLong(DateTimeFormatter.ofPattern("HHmmss").format(nowT) + (++seq).toString());
            code = member.getMbrCtgr();
        }

        calCategory = CalCategory.builder()
                .ctgrRgdt(nowT)
                .ctgrId(member.getMbrId())
                .ctgrCode(code)
                .ctgrSeqn(ctgrSeqn)
                .ctgrColr(member.getMbrColr())
                .ctgrCrdt(DateTimeFormatter.ofPattern("yyyyMMdd").format(nowT))
                .ctgrFlag('Y')
                .build();
        /*
        calCategory.setCtgrRgdt(nowT);
        calCategory.setCtgrId(member.getMbrId());
        calCategory.setCtgrCode(code);
        calCategory.setCtgrSeqn(ctgrSeqn);
        calCategory.setCtgrCrdt(DateTimeFormatter.ofPattern("yyyyMMdd").format(nowT));
        calCategory.setCtgrColr(member.getMbrColr());
        calCategory.setCtgrFlag('Y');
        */
        categoryRepository.save(calCategory);
        memberRepository.updateSeqn(member.getMbrId(), member.getMbrSeqn());

        HttpSession session = request.getSession();
        session.setAttribute(SessionConst.LOGIN_MEMBER, member);

        model.addAttribute("member",member);

        return "redirect:/Calendar";
    }

    @GetMapping("/change")
    public String calendarChange(Model model, HttpServletRequest request){
        //세션이 있으면 있는 세션 반환, 없으면 신규 세션 생성
        HttpSession session = request.getSession();
        Member m = (Member)session.getAttribute(SessionConst.LOGIN_MEMBER);

        List<CalCategory> calCategory = categoryRepository.findByCtgrId(m.getMbrId());

        model.addAttribute("member",m);
        model.addAttribute("categories",calCategory);

        return "/view/calendar/changeCalendar";
    }

    @PostMapping("/change")
    public String calendarChangePost(@ModelAttribute("mbrId")String id, @ModelAttribute("mbrSeqn")String seqn
                                    , HttpServletRequest request){

        Integer res =  memberRepository.updateSeqn(id, Long.parseLong(seqn));

        Member member = memberRepository.findByMbrId(id).get();

        HttpSession session = request.getSession();
        session.setAttribute(SessionConst.LOGIN_MEMBER, member);
        if(res == 1){
            return "true";
        }
        else{
            return "false";
        }
    }

    @PostMapping("/delete")
    public String calendarDelete(@RequestParam("ctgrId")String id, @RequestParam("ctgrSeqn")String seqn){

        LocalDateTime nowT = LocalDateTime.now();

        Integer res = categoryRepository.deleteByCtgrSeqn(id, Long.parseLong(seqn), nowT);

        if(res == 1){
            return "true";
        }
        else{
            return "false";
        }
    }
}
