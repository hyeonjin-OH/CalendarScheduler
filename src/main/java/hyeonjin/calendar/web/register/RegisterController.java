package hyeonjin.calendar.web.register;

import hyeonjin.calendar.domain.dto.MemberDTO;
import hyeonjin.calendar.domain.member.Member;
import hyeonjin.calendar.web.SessionConst;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.SQLException;
import java.util.Optional;
import java.util.regex.Pattern;

@Slf4j
@Controller
@RequiredArgsConstructor
public class RegisterController {

    //private final MembersRepository memberRepository;
    private final RegisterService registerService;

    @GetMapping("/join")
    public String register(@ModelAttribute("member")Member member){
        return "view/login/joinForm";
    }


    @PostMapping("/join")
    public String register(@Valid @ModelAttribute("member")MemberDTO member, BindingResult bindingResult,
                           HttpServletRequest request){

        if (!Pattern.matches("^(?=.*[a-z])(?=.*\\d)[a-z\\d]{6,18}$", member.getMbrId())) {
            bindingResult.rejectValue("mbrId", "required");
        }

        if (!Pattern.matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{8,20}$", member.getMbrPwd())) {
            bindingResult.rejectValue("mbrPwd", "required");
        }
        if(!Pattern.matches("^([a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+)*$", member.getMbrEmail())){
            bindingResult.rejectValue("mbrEmail", "required");
        }
        if(bindingResult.hasErrors()) {
            return "view/login/joinForm";
        }

        try{
            MemberDTO mbrDto = registerService.register(member);
        }
        catch (SQLException e){

            bindingResult.reject("RegisterFail", "회원가입에 실패하였습니다.");
            return  "view/login/joinForm";
        }

        return "redirect:/login";
    }

    @GetMapping("/update")
    public String updateProfile(Model model, HttpServletRequest request){

        //세션이 있으면 있는 세션 반환, 없으면 신규 세션 생성
        HttpSession session = request.getSession();
        Member m = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);

        model.addAttribute("member",m);

        return "view/login/updateForm";
    }

    @PostMapping("/update")
    public String updateProfile(@ModelAttribute("member")MemberDTO member, BindingResult bindingResult,
                           HttpServletRequest request){

        if(member.getMbrPwd() != ""){
            if (!Pattern.matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{8,20}$", member.getMbrPwd())) {
                bindingResult.rejectValue("mbrPwd", "required");
            }
        }
        if(!Pattern.matches("^([a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+)*$", member.getMbrEmail())){
            bindingResult.rejectValue("mbrEmail", "required");
        }
        if(bindingResult.hasErrors()) {

            return "view/login/updateForm";
        }

        MemberDTO m;
        try{
            m = registerService.updateInfo(member);
        }
        catch (Exception e){
            bindingResult.reject("updateFail");

            return "view/login/updateForm";
        }

        //업데이트 후 바뀐 멤버 정보 세션에 담기
        HttpSession session = request.getSession();
        session.setAttribute(SessionConst.LOGIN_MEMBER, m.ToEntity());

        return "redirect:/update";
    }

    @PostMapping("/login/find")
    public String updatePwd(@Valid @ModelAttribute("member")MemberDTO member, BindingResult bindingResult,
                                HttpServletRequest request){
        if(bindingResult.hasErrors()) {
            bindingResult.reject("UpdateFail");

            return "redirect:/login/find";
        }

        try{
            registerService.updatePwd(member);
            return "redirect:/login";
        }
        catch (SQLException e){
            bindingResult.reject("UpdateFail", "수정에 실패하였습니다.");
            return "redirect:/login/find";
        }
    }

    @GetMapping("/join/idchk")
    @ResponseBody
    public Boolean idDupChk(@ModelAttribute("mbrid")String mbrid){
        String id = mbrid;

        if(registerService.idDupChk(id).equals(Optional.empty())){
            return true;
        }
        else {
            return false;
        }
    }

    @GetMapping("/login/find/idVal")
    @ResponseBody
    public Boolean idValidateChk(@ModelAttribute("mbrid")String mbrid, @ModelAttribute("mbremail")String mbremail){
        String id = mbrid;
        String email = mbremail;

        if(registerService.idValidateChk(id, email).isPresent()) {
            return false;
        }
        else {
            return true;
        }
    }

}
