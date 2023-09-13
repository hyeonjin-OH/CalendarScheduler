package hyeonjin.calendar.web.register;


import hyeonjin.calendar.domain.category.CalCategory;
import hyeonjin.calendar.domain.category.CategoryRepository;
import hyeonjin.calendar.domain.dto.MemberDTO;
import hyeonjin.calendar.domain.member.Member;
import hyeonjin.calendar.domain.member.MembersRepository;
import hyeonjin.calendar.domain.member.PwdEncrypt;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class RegisterService {

    private final MembersRepository memberRepository;

    private final CategoryRepository categoryRepository;

    private final PwdEncrypt pwdEncryptClass;
    @Transactional(rollbackOn = {SQLException.class})
    public MemberDTO register(Member member) throws SQLException{
        Member newMember = new Member();
        Long seq = memberRepository.countBy();
        String encryptedPwd = pwdEncryptClass.pwdEncrpyt(member.getMbrPwd());

        LocalDateTime nowT = LocalDateTime.now();
        if(member.getMbrSeqn()== null){
            member.setMbrSeqn(Long.parseLong(DateTimeFormatter.ofPattern("HHmmss").format(nowT) + (++seq).toString()));
        }
        member.setMbrPwd(encryptedPwd);
        member.setMbrRgdt(nowT);
        try{
            newMember = memberRepository.save(member);

            CalCategory calCategory = CalCategory.builder()
                    .ctgrRgdt(newMember.getMbrRgdt())
                    .ctgrId(newMember.getMbrId())
                    .ctgrCode(newMember.getMbrCtgr())
                    .ctgrSeqn(newMember.getMbrSeqn())
                    .ctgrColr(newMember.getMbrColr())
                    .ctgrCrdt(DateTimeFormatter.ofPattern("yyyyMMdd").format(newMember.getMbrRgdt()))
                    .ctgrFlag('Y')
                    .build();

            categoryRepository.save(calCategory);
        }
        catch(Exception e){
            throw new SQLException(e);
        }

        MemberDTO dto = MemberDTO.builder()
                .id(newMember.getId())
                .mbrId(newMember.getMbrId())
                .mbrPwd(newMember.getMbrPwd())
                .mbrEmail(newMember.getMbrEmail())
                .mbrSeqn(newMember.getMbrSeqn())
                .mbrNick(newMember.getMbrNick())
                .build();

        return dto;
    }

    @Transactional(rollbackOn = {SQLException.class})
    public void saveMember(Member member) throws SQLException{
        try{
            memberRepository.save(member);
        }
        catch (Exception e){
            throw new SQLException(e);
        }
    }

    @Transactional(rollbackOn = {SQLException.class})
    public Member updatePwd(Member member) throws SQLException{

        try{
            Member orginMember = memberRepository.findByMbrId(member.getMbrId()).get();

            orginMember.setMbrEmail(member.getMbrEmail());
            orginMember.setMbrUpdt(LocalDateTime.now());
            orginMember.setMbrId(member.getMbrId());
            orginMember.setMbrPwd(member.getMbrPwd());

            //memberRepository.save(orginMember);
        }
        catch (Exception e){
            throw new SQLException(e);
        }
        //업데이트 후 바뀐 멤버 정보 세션에 담기
        Member newMember= memberRepository.findByMbrId(member.getMbrId()).get();

        return newMember;
    }

    @Transactional(rollbackOn = {SQLException.class})
    public MemberDTO updateInfo(Member member) throws SQLException{

        Member updateMem = new Member();

        try{
            Member orginMember = memberRepository.findByMbrId(member.getMbrId()).get();

            if(member.getMbrPwd()!= ""){
                orginMember.setMbrNick(member.getMbrNick());
                orginMember.setMbrUpdt(LocalDateTime.now());
                orginMember.setMbrEmail(member.getMbrEmail());
                orginMember.setMbrPwd(member.getMbrPwd());
                orginMember.setMbrId(member.getMbrId());
            }
            else{
                orginMember.setMbrNick(member.getMbrNick());
                orginMember.setMbrUpdt(LocalDateTime.now());
                orginMember.setMbrEmail(member.getMbrEmail());
                orginMember.setMbrId(member.getMbrId());
            }

            updateMem = memberRepository.save(orginMember);
        }
        catch (Exception e){
            throw new SQLException(e);
        }

        //업데이트 후 바뀐 멤버 정보 세션에 담기
        MemberDTO dto= MemberDTO.builder()
                .id(updateMem.getId())
                .mbrId(updateMem.getMbrId())
                .mbrPwd(updateMem.getMbrPwd())
                .mbrEmail(updateMem.getMbrEmail())
                .mbrSeqn(updateMem.getMbrSeqn())
                .mbrNick(updateMem.getMbrNick())
                .build();

        return dto;
    }

    public Optional<Member> idDupChk(String id){
        Optional<Member> res =  memberRepository.findByMbrId(id);
        return res;
    }

    public Optional<Member> idValidateChk(String id, String email){
        Optional<Member> res =  memberRepository.findByMbrIdAndMbrEmail(id, email);
        return res;
    }
}
