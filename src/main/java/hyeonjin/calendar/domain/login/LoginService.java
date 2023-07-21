package hyeonjin.calendar.domain.login;

import hyeonjin.calendar.domain.member.Member;
import hyeonjin.calendar.domain.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
@RequiredArgsConstructor
public class LoginService {
    private final MemberRepository memberRepository;
    public Member login(String loginId, String pwd){

        Optional<Member> byLoginId = memberRepository.findByMbrId(loginId);
        return byLoginId.filter(m->m.getMbrPwd().equals(pwd)).orElse(null);

    }
}
