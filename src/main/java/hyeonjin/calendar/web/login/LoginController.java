package hyeonjin.calendar.web.login;

import hyeonjin.calendar.domain.login.GoogleLoginService;
import hyeonjin.calendar.domain.login.LoginService;
import hyeonjin.calendar.domain.member.Member;
import hyeonjin.calendar.domain.member.MemberRepository;
import hyeonjin.calendar.domain.oauth.google.GoogleToken;
import hyeonjin.calendar.web.SessionConst;
import hyeonjin.calendar.web.argumentresolver.Login;
import hyeonjin.calendar.web.session.SessionManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Slf4j
@Controller
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;
    private final SessionManager sessionManager;
    private final MemberRepository memberRepository;

    @Value("${google.client.id}")
    private String googleClientId;
    @Value("${google.client.secret}")
    private String googleClientSecret;
    @Value("${google.rtoken.url}")
    private String googleTokenUrl;
    @Value("${google.auth.url}")
    private String googleAuthUrl;

    String redirectUrl = "http://localhost:8080/login/oauth2/google";

    private final GoogleLoginService googleLoginService;

    @GetMapping("/login")
    public String login(@ModelAttribute("loginForm") LoginForm form, @Login Member loginMember){

        if(loginMember == null){
            return "view/login/loginForm";
        }

        return "redirect:/Calendar";
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

    @GetMapping("/login/socialgoogle")
    public String loginGoogleRedirect(){
        String reqUrl = googleAuthUrl + googleClientId
                + "&redirect_uri=" + redirectUrl
                + "&response_type=code"
                //+ "&scope=email%20profile%20openid"
                + "&scope=https://www.googleapis.com/auth/userinfo.email https://www.googleapis.com/auth/userinfo.profile"
                + "&access_type=offline"
                + "&prompt=consent";

        return "redirect:"+ reqUrl;
    }

    @GetMapping("/login/oauth2/google")
    public String loginGoogle(HttpServletRequest request, @RequestParam(value = "code") String authCode,
                              HttpServletResponse response){

        LocalDateTime nowT = LocalDateTime.now();

        // WebClient 방식
        GoogleToken googleToken1 = googleLoginService.requestGoogleToken(authCode);
        Member socialM = googleLoginService.requestGoogleUserInfo(googleToken1);

        String email = socialM.getMbrEmail();
        Optional<Member> member = memberRepository.findSocialMember(email);
        Long maxSrno = memberRepository.findMaxMember();

        // email이 존재한다면 가입한 것으로 보고 로그인 없으면 바로 회원가입시켜서 로그인
        if(!member.isPresent()) {

            socialM.setMbrRgdt(nowT);
            socialM.setMbrId("social"+DateTimeFormatter.ofPattern("HHmmss").format(nowT)+ (++maxSrno).toString());
            socialM.setMbrSeqn(Long.parseLong(DateTimeFormatter.ofPattern("HHmmss").format(nowT) + (++maxSrno).toString()));

            memberRepository.save(socialM);

            return "redirect:/login";
        }
        else{
            HttpSession session = request.getSession();
            session.setAttribute(SessionConst.LOGIN_MEMBER, member.get());

            return "redirect:/Calendar";
        }

/*
        GoogleRequest googleOAuthRequest = GoogleRequest
                .builder()
                .clientId(googleClientId)
                .clientSecret(googleClientSecret)
                .code(authCode)
                .redirectUri(redirectUrl)
                .grantType("authorization_code")
                .build();


        RestTemplate restTemplate = new RestTemplate();

        //3.토큰요청을 한다.
        ResponseEntity<GoogleToken> apiResponse = restTemplate.postForEntity(googleTokenUrl + "/token"
                                                    , googleOAuthRequest, GoogleToken.class);

        //4.받은 id토큰을 토큰객체에 저장
        GoogleToken googleLoginResponse = apiResponse.getBody();
        String googleToken = googleLoginResponse.getId_token();
        String accessToken = googleLoginResponse.getAccess_token();
        String refreshToken = googleLoginResponse.getRefreshToken();
        //LocalDateTime expireTime = (LocalDateTime) nowT + googleLoginResponse.getExpires_in();

        //5.받은 토큰을 구글에 보내 유저정보를 얻는다.
        String requestUrl = UriComponentsBuilder.fromHttpUrl(googleTokenUrl + "/tokeninfo")
                .queryParam("id_token",googleToken).toUriString();

        //6.허가된 토큰의 유저정보를 결과로 받는다.
        String result = restTemplate.getForObject(requestUrl, String.class);
        JSONObject resultInfo = new JSONObject(result.trim());

        String email = resultInfo.getString("email");

        Optional<Member> member = memberRepository.findSocialMember(email);
        Long maxSrno = memberRepository.findMaxMember();

        // email이 존재한다면 가입한 것으로 보고 로그인 없으면 바로 회원가입시켜서 로그인
        if(!member.isPresent()) {
            Member newMember = member.orElse(new Member());
            newMember.setMbrRgdt(nowT);
            newMember.setMbrEmail(email);
            newMember.setMbrId("social"+DateTimeFormatter.ofPattern("HHmmss").format(nowT)+ (++maxSrno).toString());
            newMember.setMbrPwd(resultInfo.getString("sub").substring(0,16));
            newMember.setMbrSeqn(Long.parseLong(DateTimeFormatter.ofPattern("HHmmss").format(nowT) + (++maxSrno).toString()));
            newMember.setMbrNick(resultInfo.getString("name"));
            newMember.setMbrCtgr("personal");
            newMember.setMbrColr("skyblue");

            memberRepository.save(newMember);

            return "redirect:/login";

        }
        else{
            HttpSession session = request.getSession();
            session.setAttribute(SessionConst.LOGIN_MEMBER, member.get());

            return "redirect:/Calendar";
        }

 */
    }

    @PostMapping("/login/oauth2/google")
    public String loginGoogleToken(){

        return "";
    }

}
