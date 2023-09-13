package hyeonjin.calendar.web.register;

import com.fasterxml.jackson.databind.ObjectMapper;
import hyeonjin.calendar.WebConfig;
import hyeonjin.calendar.domain.dto.MemberDTO;
import hyeonjin.calendar.domain.jwt.JwtInterceptor;
import hyeonjin.calendar.domain.member.Member;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.BindingResult;

import java.time.LocalDateTime;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(value = {RegisterController.class},
excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebConfig.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtInterceptor.class)
})
//@AutoConfigureWebMvc
class RegisterControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    RegisterService registerService;

    @Test
    @WithMockUser(roles = "USER")
    void getregister() throws Exception {
        mockMvc.perform(get("/join").accept(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(view().name("view/login/joinForm"))
                .andDo(print());
    }

    @Test
    @WithMockUser(roles = "USER")
    void postRegister() throws Exception{

        BindingResult result = mock(BindingResult.class);
        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);

        Member m = Member.builder()
                //.id(236L)
                .mbrRgdt(LocalDateTime.now())
                .mbrId("test222")
                .mbrPwd("test222@@")
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

        //Controller 내부에 있는 service 구현부분은 mock객체로 반환을 해줬다고 가정한다.
        given(registerService.register(m)).willReturn(dto);

        byte[] str = objectMapper.writeValueAsBytes(m);

        mockMvc.perform(post("/join")
                        .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .flashAttr("member", m)
                    .content(objectMapper.writeValueAsBytes(m)))

                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/login"))
                .andDo(print());
    }
}