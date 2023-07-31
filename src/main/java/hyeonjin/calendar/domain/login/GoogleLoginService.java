package hyeonjin.calendar.domain.login;

import hyeonjin.calendar.domain.dto.GoogleMemberDto;
import hyeonjin.calendar.domain.member.Member;
import hyeonjin.calendar.domain.member.MemberRepository;
import hyeonjin.calendar.domain.oauth.google.GoogleRequest;
import hyeonjin.calendar.domain.oauth.google.GoogleToken;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service("google")
@RequiredArgsConstructor
public class GoogleLoginService {
    @Value("${google.client.id}")
    private String googleClientId;
    @Value("${google.client.secret}")
    private String googleClientSecret;
    @Value("${google.rtoken.url}")
    private String googleTokenUrl;
    @Value("${google.auth.url}")
    private String googleAuthUrl;
    @Value("${google.user.url}")
    private String googleUserUrl;
    String redirectUrl = "http://localhost:8080/login/oauth2/google";

    private final MemberRepository memberRepository;

    private final WebClient webClient;


    public GoogleToken requestGoogleToken(String authCode){
    /*
        String requestJson = webClient.post()
                .uri(googleTokenUrl+"/token")
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(GoogleRequest
                        .builder()
                        .clientId(googleClientId)
                        .clientSecret(googleClientSecret)
                        .code(authCode)
                        .redirectUri(redirectUrl)
                        .grantType("authorization_code")
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .block();
        */

        GoogleToken googleRequest = webClient.post()
                .uri(googleTokenUrl+"/token")
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(GoogleRequest
                        .builder()
                        .clientId(googleClientId)
                        .clientSecret(googleClientSecret)
                        .code(authCode)
                        .redirectUri(redirectUrl)
                        .grantType("authorization_code")
                        .build())
                .retrieve()
                .bodyToMono(GoogleToken.class)
                .blockOptional()
                .orElseThrow(IllegalArgumentException::new);    //임시

        return googleRequest;

    }

    public Member requestGoogleUserInfo(GoogleToken accessToken){
/*
        String responseJson = webClient.get()
                .uri(googleUserUrl)
                .header(HttpHeaders.AUTHORIZATION, "Bearer "+accessToken.getAccess_token())
                .retrieve()
                .bodyToMono(String.class)
                .block();
*/
        GoogleMemberDto googleMember = webClient.get()
                .uri(googleUserUrl)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer "+accessToken.getAccess_token())
                .retrieve()
                .bodyToMono(GoogleMemberDto.class)
                .blockOptional()
                .orElseThrow(IllegalArgumentException::new);

        return googleMember.toEntity(accessToken);
    }




}
