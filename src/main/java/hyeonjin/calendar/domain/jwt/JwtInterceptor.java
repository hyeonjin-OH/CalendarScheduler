package hyeonjin.calendar.domain.jwt;

import hyeonjin.calendar.domain.member.MemberRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.JwtValidationException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtProvider jwtProvider;
    private final MemberRepository memberRepository;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        String token = jwtProvider.resolveToken(request, HttpHeaders.AUTHORIZATION);

        if(!Strings.isNotBlank(token)){
            response.setStatus((HttpStatus.FORBIDDEN.value()));
            return false;
        }

        try{
            String accessToken = jwtProvider.resolveToken(request,"access_token");;

            if (StringUtils.hasText(accessToken)) {
                if (jwtProvider.validateToken(accessToken)) {
                    //인증정보 셋팅
                    Map<String, String> auth = jwtProvider.userinfoToken(accessToken);
                    request.setAttribute("socialid", auth.get("socialid"));
                    request.setAttribute("email", auth.get("email"));
                }
            }
        }catch (ExpiredJwtException e){
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }catch(JwtValidationException ex){
            response.setStatus(HttpStatus.FORBIDDEN.value());
            return false;
        }

        return true;
    }
}
