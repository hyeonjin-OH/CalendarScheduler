package hyeonjin.calendar.web;

import hyeonjin.calendar.domain.member.Member;
import hyeonjin.calendar.domain.member.MemberRepository;
import hyeonjin.calendar.web.argumentresolver.Login;
import hyeonjin.calendar.web.session.SessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {

    private final MemberRepository memberRepository;
    private final SessionManager sessionManager;


    @GetMapping("/")
    public String homeLoginV3ArgumentResolver(@Login Member loginMember, Model model){

        //세션에 회원 데이터가 없으면
        if(loginMember == null){
            return "index";
        }

        model.addAttribute("member", loginMember);
        return "redirect:/Calendar";
    }
}