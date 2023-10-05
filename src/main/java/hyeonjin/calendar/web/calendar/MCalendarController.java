package hyeonjin.calendar.web.calendar;

import hyeonjin.calendar.domain.category.CalCategory;
import hyeonjin.calendar.domain.category.CategoryRepository;
import hyeonjin.calendar.domain.member.Member;
import hyeonjin.calendar.domain.member.MembersRepository;
import hyeonjin.calendar.domain.schedule.Schedule;
import hyeonjin.calendar.domain.schedule.SchedulesRepository;
import hyeonjin.calendar.web.SessionConst;
import hyeonjin.calendar.web.register.RegisterServiceImpl;
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

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;

@Controller
@Slf4j
@RequestMapping("/Calendar")
@RequiredArgsConstructor
public class MCalendarController {

    //private final MemberRepository memberRepository;
    private final MembersRepository memberRepository;
    private final CategoryRepository categoryRepository;

    private final SchedulesRepository schedulesRepository;

    private final SessionManager sessionManager;
    private final CalendarService calendarService;
    private final RegisterServiceImpl registerServiceImpl;

    @GetMapping
    public String scheduleInfo(@ModelAttribute("schedule") Schedule scd,
                               HttpServletRequest request, Model model) {

        //세션이 있으면 있는 세션 반환, 없으면 신규 세션 생성
        HttpSession session = request.getSession();
        Member m = (Member)session.getAttribute(SessionConst.LOGIN_MEMBER);

        CalCategory ctgrInfo = categoryRepository.findByCtgrIdAndCtgrSeqn(m.getMbrId(),m.getMbrSeqn()).get();

        Long ctgSeqn = ctgrInfo.getCtgrSeqn();
        String ctgColr = ctgrInfo.getCtgrColr();

        List<Schedule> schedule = schedulesRepository.findByScdSeqnAndScdFlag(ctgSeqn,"Y");

        model.addAttribute("member", m);

        scd.setScdSeqn(ctgSeqn);
        scd.setScdId(m.getMbrId());
        scd.setScdColr(ctgColr);

        return "view/calendar/fullCalendar";
    }

    @RequestMapping(value="/schedules", method = RequestMethod.GET)
    @ResponseBody
    public String calendarInfo(HttpServletRequest request) {

        //세션이 있으면 있는 세션 반환, 없으면 신규 세션 생성
        HttpSession session = request.getSession();
        Member m = (Member)session.getAttribute(SessionConst.LOGIN_MEMBER);

        String mbrId = m.getMbrId();
        Long mbrSeqn = memberRepository.findSeqn(mbrId);

        CalCategory ctgrInfo = categoryRepository.findByCtgrIdAndCtgrSeqn(mbrId,mbrSeqn).get();
        List<Schedule> schedule = schedulesRepository.findByScdSeqnAndScdFlag(ctgrInfo.getCtgrSeqn(),"Y");
        JSONArray jsonArr = insertSchedulesJson(schedule);

        return jsonArr.toString();
    }

    private JSONArray insertSchedulesJson(List<Schedule> schedule) {
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
            temp.put("nick",memberRepository.findNickName(schedule.get(i).getScdId()));
            temp.put("id", schedule.get(i).getId());
            temp.put("scdWkno", schedule.get(i).getScdWkno());
            hash.put("extendedProps", temp);
            jsonObj = new JSONObject(hash);
            jsonArr.put(jsonObj);
        }
        return jsonArr;
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
            code = categoryRepository.findByCtgrSeqn(ctgrSeqn).stream().findFirst().get().getCtgrCode();
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


        Member updateMember = memberRepository.findByMbrId(member.getMbrId()).get();
        updateMember.setMbrUpdt(LocalDateTime.now());
        updateMember.setMbrSeqn(ctgrSeqn);
        calendarService.AddPost(calCategory, updateMember);

        member = memberRepository.findByMbrId(member.getMbrId()).get();

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
    @ResponseBody
    public String calendarChangePost(@RequestBody Member m
                                    , HttpServletRequest request, Model model){

        try{
            Member updateMember = memberRepository.findByMbrId(m.getMbrId()).get();
            updateMember.setMbrUpdt(LocalDateTime.now());
            updateMember.setMbrSeqn(m.getMbrSeqn());
            registerServiceImpl.saveMember(updateMember);
        }
        catch (SQLException e){
            return "false";
        }

        Member member = memberRepository.findByMbrId(m.getMbrId()).get();

        HttpSession session = request.getSession();
        session.setAttribute(SessionConst.LOGIN_MEMBER, member);

        List<CalCategory> calCategory = categoryRepository.findByCtgrId(member.getMbrId());

        model.addAttribute("member",member);
        model.addAttribute("categories",calCategory);


        return "true";
        /*
        if(res == 1){
            return "true";
        }
        else{
            return "false";
        }
         */
    }

    @GetMapping("/delete")
    public String CalendarReload(Model model, HttpServletRequest request){
        HttpSession session = request.getSession();
        Member m = (Member)session.getAttribute(SessionConst.LOGIN_MEMBER);

        List<CalCategory> calCategory = categoryRepository.findByCtgrId(m.getMbrId());

        model.addAttribute("member",m);
        model.addAttribute("categories",calCategory);

        return "/view/calendar/changeCalendar";
    }

    @PostMapping("/delete")
    @ResponseBody
    public String calendarDelete(@RequestParam("ctgrId")String id, @RequestParam("ctgrSeqn")String seqn
                                ,Model model){

        LocalDateTime nowT = LocalDateTime.now();

        Integer res = categoryRepository.deleteByCtgrSeqn(id, Long.parseLong(seqn), nowT);

        Member member = memberRepository.findByMbrId(id).get();
        List<CalCategory> calCategory = categoryRepository.findByCtgrId(id);

        model.addAttribute("member",member);
        model.addAttribute("categories",calCategory);

        if(res == 1){
            return "true";
        }
        else{
            return "false";
        }
    }

    @PostMapping("/find")
    @ResponseBody
    public String calendarFind(@RequestParam("findTxt") String findTxt, @RequestParam("seqn")String seqn){

        List<Schedule> schedule = schedulesRepository.findByScdSeqnAndScdCnts(Long.parseLong(seqn), "%"+findTxt+"%");

        HashMap<String, Object> hash = new HashMap<>();

        JSONObject jsonObj = new JSONObject();
        JSONArray jsonArr = new JSONArray();

        for (int i = 0; i < schedule.size(); i++) {
            hash.put("title", schedule.get(i).getScdTitle());
            hash.put("start", schedule.get(i).getScdFrdt());
            hash.put("frtm", schedule.get(i).getScdFrtm());
            hash.put("contents",schedule.get(i).getScdCnts());
            hash.put("mbrId",schedule.get(i).getScdId());
            hash.put("nick",memberRepository.findNickName(schedule.get(i).getScdId()));

            jsonObj = new JSONObject(hash);
            jsonArr.put(jsonObj);
        }

        return jsonArr.toString();
    }

    public <T> T testTemplate(T initVal){
        T res = initVal;
        return res;
    }
}
