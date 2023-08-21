package hyeonjin.calendar.web.schedule;

import hyeonjin.calendar.domain.category.CalCategory;
import hyeonjin.calendar.domain.category.CategoryRepository;
import hyeonjin.calendar.domain.member.Member;
import hyeonjin.calendar.domain.schedule.Schedule;
import hyeonjin.calendar.domain.schedule.SchedulesRepository;
import hyeonjin.calendar.web.SessionConst;
import hyeonjin.calendar.web.session.SessionManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/schedulePop")
@RequiredArgsConstructor
public class ScheduleController {
    private final CategoryRepository categoryRepository;

    //private final ScheduleRepository scheduleRepository;
    private final SchedulesRepository schedulesRepository;
    private final SessionManager sessionManager;


    @PostMapping
    public String scheduleInsert(@ModelAttribute("schedule") Schedule schedule,
                                 BindingResult bindingResult, HttpServletRequest request
            , Model model){
        //세션이 있으면 있는 세션 반환, 없으면 신규 세션 생성
        HttpSession session = request.getSession();
        Member m = (Member)session.getAttribute(SessionConst.LOGIN_MEMBER);
        LocalDateTime nowT = LocalDateTime.now();

        String mbrId = m.getMbrId();
        String mbrNick = m.getMbrNick();
        Long mbrSeqn = m.getMbrSeqn();

        Optional<CalCategory> mbrCtgr = categoryRepository.findByCtgrIdAndCtgrSeqn(mbrId, mbrSeqn);
        CalCategory ctgrInfo = mbrCtgr.get();

        Long ctgSeqn = ctgrInfo.getCtgrSeqn();
        String ctgColr = ctgrInfo.getCtgrColr();

        if(schedule.getScdFrtmchk()){
            schedule.setScdFrtm("00:00:00");
        }
        if(schedule.getScdTotmchk()){
            schedule.setScdTotm("23:59:59");
        }
        Long wkno = schedulesRepository.countByScdSeqn(ctgSeqn);
        schedule.setScdRgdt(nowT);
        schedule.setScdFlag("Y");
        schedule.setScdWkno(wkno);

        model.addAttribute("member", m);
        model.addAttribute("scdSeqn", ctgSeqn);
        model.addAttribute("scdColr", ctgColr);
        model.addAttribute("scdId", mbrId);


        //Schedule saveSchedule = scheduleRepository.save(schedule);
        Schedule saveSchedule = schedulesRepository.save(schedule);

        return "redirect:/Calendar";
    }

    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public String scheduleUpdate(@RequestBody Schedule schedule,
                                 HttpServletRequest request, Model model){
        //세션이 있으면 있는 세션 반환, 없으면 신규 세션 생성
        HttpSession session = request.getSession();
        Member m = (Member)session.getAttribute(SessionConst.LOGIN_MEMBER);

        String mbrId = m.getMbrId();
        Long mbrSeqn = m.getMbrSeqn();

        Optional<CalCategory> mbrCtgr = categoryRepository.findByCtgrIdAndCtgrSeqn(mbrId, mbrSeqn);
        CalCategory ctgrInfo = mbrCtgr.get();

        Long ctgSeqn = ctgrInfo.getCtgrSeqn();
        String ctgColr = ctgrInfo.getCtgrColr();
        /*
        if(schedule.getScdFrtmchk()){
            schedule.setScdFrtm("00:00:00");
        }
        if(schedule.getScdTotmchk()){
            schedule.setScdTotm("23:59:00");
        }
        */
        Schedule updateSchedule = schedulesRepository.findByScdWknoAndScdSeqn(schedule.getScdWkno(), schedule.getScdSeqn());

        if(schedule.getScdFrtmchk()){
            updateSchedule.setScdFrtm("00:00:00");
        }
        if(schedule.getScdTotmchk()){
            updateSchedule.setScdTotm("23:59:00");
        }
        updateSchedule.setScdCnts(schedule.getScdCnts());
        updateSchedule.setScdUpdt(LocalDateTime.now());
        updateSchedule.setScdTitle(schedule.getScdTitle());
        updateSchedule.setScdFrdt(schedule.getScdFrdt());
        updateSchedule.setScdTodt(schedule.getScdTodt());
        updateSchedule.setScdFrtm(schedule.getScdFrtm());
        updateSchedule.setScdTotm(schedule.getScdTotm());

        model.addAttribute("member", m);
        model.addAttribute("scdSeqn", ctgSeqn);
        model.addAttribute("scdColr", ctgColr);
        model.addAttribute("scdId", mbrId);
        //model.addAttribute("schedules",schedule);

        schedulesRepository.save(updateSchedule);
        //Integer res = scheduleRepository.updateById(schedule);

        return "redirect:/Calendar";
    }


    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public String scheduleDelete(@RequestBody Schedule schedule,
                                 HttpServletRequest request, Model model){
        //세션이 있으면 있는 세션 반환, 없으면 신규 세션 생성
        HttpSession session = request.getSession();
        Member m = (Member)session.getAttribute(SessionConst.LOGIN_MEMBER);

        String mbrId = m.getMbrId();
        Long mbrSeqn = m.getMbrSeqn();

        Optional<CalCategory> mbrCtgr = categoryRepository.findByCtgrIdAndCtgrSeqn(mbrId, mbrSeqn);
        CalCategory ctgrInfo = mbrCtgr.get();

        Long ctgSeqn = ctgrInfo.getCtgrSeqn();
        String ctgColr = ctgrInfo.getCtgrColr();

        model.addAttribute("member", m);
        model.addAttribute("scdSeqn", ctgSeqn);
        model.addAttribute("scdColr", ctgColr);
        model.addAttribute("scdId", mbrId);
        //model.addAttribute("schedules",schedule);

        Schedule updateSchedule = schedulesRepository.findByScdWknoAndScdSeqn(schedule.getScdWkno(), schedule.getScdSeqn());

        if(schedule.getScdFrtmchk()){
            updateSchedule.setScdFrtm("00:00:00");
        }
        if(schedule.getScdTotmchk()){
            updateSchedule.setScdTotm("23:59:00");
        }
        updateSchedule.setScdFlag("N");
        updateSchedule.setScdUpdt(LocalDateTime.now());

        model.addAttribute("member", m);
        model.addAttribute("scdSeqn", ctgSeqn);
        model.addAttribute("scdColr", ctgColr);
        model.addAttribute("scdId", mbrId);

        schedulesRepository.save(updateSchedule);
        //Integer res = scheduleRepository.deleteById(schedule);

        return "redirect:/Calendar";
    }
}
