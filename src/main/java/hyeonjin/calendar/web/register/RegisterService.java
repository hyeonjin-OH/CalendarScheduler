package hyeonjin.calendar.web.register;

import hyeonjin.calendar.domain.dto.MemberDTO;
import hyeonjin.calendar.domain.member.Member;
import jakarta.transaction.Transactional;

import java.sql.SQLException;
import java.util.Optional;

public interface RegisterService {

    MemberDTO register(MemberDTO member) throws SQLException;

    @Transactional(rollbackOn = {SQLException.class})
    void saveMember(Member member) throws SQLException;

    MemberDTO updatePwd(MemberDTO member) throws SQLException;

    MemberDTO updateInfo(MemberDTO member) throws SQLException;

    Optional<Member> idDupChk(String id);


    Optional<Member> idValidateChk(String id, String email);
}
