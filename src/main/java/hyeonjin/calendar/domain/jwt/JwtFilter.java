package hyeonjin.calendar.domain.jwt;

import hyeonjin.calendar.domain.member.Member;
import hyeonjin.calendar.domain.member.MemberRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final MemberRepository memberRepository;
    private final String NO_CHECK_URL = "/login";



    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        if(request.getRequestURI().equals(NO_CHECK_URL)) {
            filterChain.doFilter(request, response);
            return;
        }

        try{
            /// front에서 넘어온 request header에 refresh-token이 있으면 access-token이 만료된 것으로 보고 access재발급
            String jwt = jwtProvider.resolveToken(request, "refresh_token");

            if (StringUtils.hasText(jwt)) {  //refresh 토큰이 있으면
                if (jwtProvider.validateToken(jwt)) {  // 토큰을 검증하고 (올바르지 않은 경우 여기서 예외 발생)
                    //refresh토큰으로 회원 확인 후 access토큰 재발행
                    Optional<Member> mem = memberRepository.findByRefreshToken(jwt);

                    if(mem.isPresent()){
                        String id = mem.get().getMbrId();
                        String email = mem.get().getMbrEmail();

                        //회원이 존재한다면 해당 정보로 accessToken발행
                        String reAccessToken = jwtProvider.createAccessToken(id, email);
                        response.setHeader("access_token", reAccessToken);

                        //refresh 토큰의 만료일자 확인 후 하루 미만으로 남으면 재발행
                            String reRefreshToken = jwtProvider.reIssueRefreshToken(jwt);
                            response.setHeader("refresh_token", reRefreshToken);
                            memberRepository.updateRefreshToken(id, email, reRefreshToken);
                        return;
                    }
                }
            }
            //refreshtoken이 없으면 accesstoken여부 확인하여 인증처리
            else {
                String accessjwt = jwtProvider.resolveToken(request, "access_token");
                if (StringUtils.hasText(accessjwt)) {
                    if (jwtProvider.validateToken(accessjwt)) {
                        //인증정보 셋팅
                        Map<String, String> auth = jwtProvider.userinfoToken(accessjwt);
                        request.setAttribute("socialid", auth.get("socialid"));
                        request.setAttribute("email", auth.get("email"));

                        //인증정보 조회 후 securitycontext에 저장
                        jwtProvider.saveAuthentication(memberRepository.findByMbrIdAndEmail(auth.get("socialid"), auth.get("email")).get());
                        filterChain.doFilter(request, response);
                    }
                }
            }
        }
        catch (ExpiredJwtException e){
        }

        // 토큰이 없으면 SecurityContext에 인증 객체 넣지 않고 그냥 필터 진행
        filterChain.doFilter(request, response);
    }


}
