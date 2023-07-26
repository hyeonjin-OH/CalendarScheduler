package hyeonjin.calendar.web.login;

import hyeonjin.calendar.domain.login.LoginService;
import hyeonjin.calendar.domain.member.Member;
import hyeonjin.calendar.web.SessionConst;
import hyeonjin.calendar.web.session.SessionManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;
    private final SessionManager sessionManager;

    @GetMapping("/login")
    public String login(@ModelAttribute("loginForm") LoginForm form){
        return "view/login/loginForm";
    }

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute("loginForm") LoginForm form, BindingResult bindingResult,
                        HttpServletRequest request){
        if(bindingResult.hasErrors()) {
            bindingResult.reject("notnull", "기입하지 않은 정보가 있습니다.");
            return "view/login/loginForm";
        }

        Member loginMember = loginService.login(form.getLoginId(), form.getPassword());

        if(loginMember == null){
            bindingResult.reject("loginFail", "ID 또는 PW가 맞지 않습니다.");

            return "view/login/loginForm";
        }

        //세션이 있으면 있는 세션 반환, 없으면 신규 세션 생성
        HttpSession session = request.getSession();
        //세션에 로그인 회원 정보 보관
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);

        return "redirect:/Calendar";
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request){

        HttpSession session = request.getSession(false);
        if(session != null){
            session.invalidate();
        }

        return "redirect:/login";
    }

    @GetMapping("/login/find")
    public String findAccount(@ModelAttribute("member")Member member){

        return "view/login/findForm";
    }


}
