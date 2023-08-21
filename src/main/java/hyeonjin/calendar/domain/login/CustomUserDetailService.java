package hyeonjin.calendar.domain.login;

import hyeonjin.calendar.domain.member.Member;
import hyeonjin.calendar.domain.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return memberRepository.findByMbrEmail(email)
                .map(this::SecurityContextUser)
                .orElseThrow();
    }

    private User SecurityContextUser(Member member){
       // List<GrantedAuthority> grantedAuthorityList
       //         = Collections.singletonList(new SimpleGrantedAuthority(member.getRole()));

        //return new User(member.getMbrEmail(), member.getMbrPwd(), grantedAuthorityList);
        return new User(member.getMbrEmail(), member.getMbrPwd(), null);

    }
}
