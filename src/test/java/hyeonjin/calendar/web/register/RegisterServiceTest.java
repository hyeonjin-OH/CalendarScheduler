package hyeonjin.calendar.web.register;

import hyeonjin.calendar.domain.category.CalCategory;
import hyeonjin.calendar.domain.category.CategoryRepository;
import hyeonjin.calendar.domain.member.Member;
import hyeonjin.calendar.domain.member.MembersRepository;
import hyeonjin.calendar.domain.member.PwdEncrypt;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import java.sql.SQLException;
import java.time.LocalDateTime;


@DataJpaTest
//@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
class RegisterServiceTest {
    @Autowired(required = false)
    RegisterService registerService;
    @Autowired(required = false)
    MembersRepository memberRepository;
    @Autowired(required = false)
    CategoryRepository categoryRepository;

    @Autowired(required = false)
    PwdEncrypt PwdEncryptClass;

    Member setMember1;
    CalCategory setCategory = new CalCategory();

    EmbeddedDatabase db;
    public void setUP(){
        //toby LIST 7-70.
        db = new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .addScript("classpath:/.../schema.sql")
                .build();
    }

    public void tearDown(){
        db.shutdown();
    }

    @Autowired(required = false)
    TestEntityManager em;

    Long cnt = 0L;

    @BeforeAll
    void Init(){
        setMember1 = Member.builder()
                //.id(134L)
                .mbrRgdt(LocalDateTime.now())
                .mbrId("test111")
                .mbrNick("테스트")
                .mbrEmail("test123@test.com")
                .mbrSeqn(2105031L)
                .mbrCtgr("personal")
                .mbrColr("pink")
                .build();

        memberRepository.save(setMember1);
        cnt = memberRepository.countBy();

        setCategory = CalCategory.builder()
                .ctgrRgdt(LocalDateTime.now())
                .ctgrCode("personal")
                .ctgrId("test111")
                .ctgrCrdt("20230814")
                .ctgrColr("pink")
                .ctgrFlag('Y')
                .ctgrSeqn(2105031L)
                .build();
        categoryRepository.save(setCategory);
    }

    @AfterAll
    void clear(){
        memberRepository.delete(setMember1);
        categoryRepository.delete(setCategory);
    }

/*
    @Test
    @DisplayName("회원가입 시 카테고리 등록 문제 발생 시 롤백여부")
    @Transactional(rollbackOn = {SQLException.class})
    void register(){

        registerService = new RegisterService(memberRepository, categoryRepository);

            Member setMember2 = Member.builder()
                    //.id(138L)
                    .mbrRgdt(LocalDateTime.now())
                    .mbrId("test222")
                    .mbrNick("테스트2")
                    .mbrEmail("test222@test.com")
                    .mbrSeqn(21050316L)
                    .mbrCtgr("personal")
                    .mbrColr("pink")
                    .build();

            try{
                memberRepository.save(setMember2);
                CalCategory calCategory2 = CalCategory.builder()
                        .ctgrRgdt(setMember2.getMbrRgdt())
                        .ctgrId(setMember2.getMbrId())
                        .ctgrCode(setMember2.getMbrCtgr())
                        .ctgrSeqn(setMember2.getMbrSeqn())
                        .ctgrColr(setMember2.getMbrColr())
                        .ctgrCrdt(DateTimeFormatter.ofPattern("yyyyMMdd").format(setMember2.getMbrRgdt()))
                        .build();

                categoryRepository.save(calCategory2);
            }
            catch (Exception e){
                em.clear();
                return;
            }
    }

    @Test
    @DisplayName("AssertThat")
    void res(){
        checkRes();
    }
*/

    @Test
    @DisplayName("회원가입 시 카테고리 등록 문제 발생 시 롤백여부2")
    void register2(){

        registerService = new RegisterService(memberRepository, categoryRepository, PwdEncryptClass);

        Member setMember2 = Member.builder()
                .mbrRgdt(LocalDateTime.now())
                .mbrId("test222")
                .mbrNick("테스트2")
                .mbrEmail("test222@test.com")
                .mbrSeqn(21050316L)
                .mbrCtgr("personal")
                .mbrColr("pink")
                .build();

        try{
                registerService.register(setMember2);
        }
        catch (SQLException e){
            checkRes();
            return;
        }
    }

    void checkRes(){
        Assertions.assertThat(memberRepository.findNickName("test111")).isEqualTo("테스트");
        Assertions.assertThat(memberRepository.countBy()).isEqualTo(cnt);
        Assertions.assertThat(memberRepository.findNickName("test222")).isEqualTo(null);
    }
}