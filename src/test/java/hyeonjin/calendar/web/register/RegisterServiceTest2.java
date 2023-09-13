package hyeonjin.calendar.web.register;

import hyeonjin.calendar.domain.category.CategoryRepository;
import hyeonjin.calendar.domain.dto.MemberDTO;
import hyeonjin.calendar.domain.member.Member;
import hyeonjin.calendar.domain.member.MembersRepository;
import hyeonjin.calendar.domain.member.PwdEncrypt;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.sql.SQLException;
import java.time.LocalDateTime;

@ExtendWith(SpringExtension.class)
@Import(value={RegisterService.class, PwdEncrypt.class})
class RegisterServiceTest2 {

    @MockBean
    MembersRepository membersRepository;

    @MockBean
    CategoryRepository categoryRepository;

    @Autowired
    RegisterService registerService;

    @Test
    public void doRegister() throws SQLException {

        Member m = Member.builder()
                .mbrRgdt(LocalDateTime.now())
                .mbrId("test222")
                .mbrPwd("test222@@")
                .mbrNick("테스트2")
                .mbrEmail("test222@test.com")
                .mbrSeqn(21050316L)
                .mbrCtgr("personal")
                .mbrColr("pink")
                .build();

        Member rm = Member.builder()
                .mbrRgdt(LocalDateTime.now())
                .mbrId("test222")
                .mbrPwd("86dcef5c913a189807b654415117f5f6f5fafc442323fbe889dc74eeceab1461")
                .mbrNick("테스트2")
                .mbrEmail("test222@test.com")
                .mbrSeqn(21050316L)
                .mbrCtgr("personal")
                .mbrColr("pink")
                .build();

        MemberDTO dto = MemberDTO.builder()
                .mbrId("test222")
                .mbrPwd("86dcef5c913a189807b654415117f5f6f5fafc442323fbe889dc74eeceab1461")
                .mbrEmail("test222@test.com")
                .mbrSeqn(21050316L)
                .build();

        Mockito.when(membersRepository.save(m)).thenReturn(rm);

        MemberDTO res = registerService.register(m);

        Assertions.assertThat(res.getMbrEmail()).isEqualTo("test222@test.com");


    }



}