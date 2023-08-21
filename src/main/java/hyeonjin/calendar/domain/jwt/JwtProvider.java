package hyeonjin.calendar.domain.jwt;


import com.nimbusds.oauth2.sdk.token.AccessTokenType;
import hyeonjin.calendar.domain.member.Member;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtProvider {
    private static final String SUBJECT_ACCESS = "access_token";
    private static final String SUBJECT_REFRESH = "refresh_token";
    private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

    @Value("${jwt.token.access-expire-time}")
    private long ACCESS_TOKEN_EXPIRE_TIME;

    @Value("${jwt.token.refresh-expire-time}")
    private long REFRESH_TOKEN_EXPIRE_TIME;

    @Value("${jwt.token.secret-key}")
    private String secretKey;

    private Key key;

    public Date get_expiredTime() {
        return _expiredTime;
    }

    public void set_expiredTime(Date _expiredTime) {
        this._expiredTime = _expiredTime;
    }

    private Date _expiredTime = new Date();

    @PostConstruct
    public void Init() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String createAccessToken(String socialid, String email) {
        long now = new Date().getTime();
        Date accessTokenExpireTime = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);
        _expiredTime = accessTokenExpireTime;
        return Jwts.builder()
                .setSubject(SUBJECT_ACCESS)
                .claim("email", email)
                .claim("socailid", socialid)
                //.claim(AUTHORITIES_KEY, authorities)
                .setExpiration(accessTokenExpireTime)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public String createRefreshToken() {
        long now = new Date().getTime();
        Date refreshTokenExpireTime = new Date(now + REFRESH_TOKEN_EXPIRE_TIME);

        return Jwts.builder()
                .setSubject(SUBJECT_REFRESH)
                .setExpiration(refreshTokenExpireTime)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    // 요청 헤더에서 토큰 꺼내오기
    public String resolveToken(HttpServletRequest request, String header) {
        String bearerToken = request.getHeader(header);

        if (StringUtils.hasText(bearerToken)
                && bearerToken.startsWith(AccessTokenType.BEARER.getValue())) {
            return bearerToken.substring(7);
        }

        return "";
    }

    // 토큰 검증하기
    public Boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);

            return true;
        } catch (ExpiredJwtException e) {
            return false;
            //throw new JwtNotAvailable("토큰이 만료되었습니다. 다시 로그인 해주세요.");
        } catch (SecurityException | MalformedJwtException
                 | IllegalArgumentException | UnsupportedJwtException e) {
            return false;
            //throw new JwtNotAvailable("올바르지 않은 토큰입니다.");
        }
    }

    public Map<String, String>userinfoToken(String accessToken){
        try{
             Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJwt(accessToken)
                    .getBody();

            Map<String, String> info = new HashMap<>();
             String mbrEmail = claims.get("email", String.class);
             String mbrId = claims.get("socialid", String.class);

             info.put("email", mbrEmail);
             info.put("socialid", mbrId);

             return info;
        }
        catch (Exception e){
            return null;
        }
    }

    // refreshtoken만료시간 확인
    public LocalDateTime checkRefreshExpireTime(String refreshToken)
    {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJwt(refreshToken)
                .getBody();

        return claims.get("exp", LocalDateTime.class);
    }

    //refreshToken재발급
    public String reIssueRefreshToken(String refreshToken){
        if(checkRefreshExpireTime(refreshToken).isBefore(LocalDateTime.now().minusDays(1))){
            return createRefreshToken();
        }
        else {
            return refreshToken;
        }
    }

    public void saveAuthentication(Member member) {

        UserDetails userDetailsUser = org.springframework.security.core.userdetails.User.builder()
                .username(member.getMbrEmail())
                .password(member.getMbrPwd())
                .roles(member.getMbrRole())
                .build();

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(userDetailsUser, null, authoritiesMapper.mapAuthorities(userDetailsUser.getAuthorities()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
