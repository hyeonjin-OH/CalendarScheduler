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
    public MemberDTO register(MemberDTO member) throws SQLException{
        Member newMember;
        Long seq = memberRepository.countBy();
        String encryptedPwd = pwdEncryptClass.pwdEncrpyt(member.getMbrPwd());

        LocalDateTime nowT = LocalDateTime.now();
        if(member.getMbrSeqn()== null){
            member.setMbrSeqn(Long.parseLong(DateTimeFormatter.ofPattern("HHmmss").format(nowT) + (++seq).toString()));
        }
        member.encryptPwd(encryptedPwd);
        member.setMbrRgdt(nowT);

        Member saveMember = member.ToEntity();

        try{
            newMember = memberRepository.save(saveMember);

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

        MemberDTO dto = newMember.ToDto();

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
    public MemberDTO updatePwd(MemberDTO member) throws SQLException{

        try{
            MemberDTO orginMember = memberRepository.findByMbrId(member.getMbrId()).get().ToDto();
            orginMember.UpdatePwd(member.getMbrId(), member.getMbrEmail(), member.getMbrPwd(), LocalDateTime.now());
            orginMember.ToEntity();
        }
        catch (Exception e){
            throw new SQLException(e);
        }
        MemberDTO newMember= memberRepository.findByMbrId(member.getMbrId()).get().ToDto();

        return newMember;
    }

    @Transactional(rollbackOn = {SQLException.class})
    public MemberDTO updateInfo(MemberDTO member) throws SQLException{

        Member updateMem;
        MemberDTO setMember;

        try{
            Member orginMember = memberRepository.findByMbrId(member.getMbrId()).get();
            setMember = MemberDTO.allBuilder()
                            .id(orginMember.getId())
                            .mbrRgdt(orginMember.getMbrRgdt())
                            .mbrUpdt(orginMember.getMbrUpdt())
                            .mbrId(orginMember.getMbrId())
                            .mbrPwd(orginMember.getMbrPwd())
                            .mbrNick(orginMember.getMbrNick())
                            .mbrEmail(orginMember.getMbrEmail())
                            .mbrSeqn(orginMember.getMbrSeqn())
                            .mbrColr(orginMember.getMbrColr())
                            .mbrCtgr(orginMember.getMbrCtgr())
                            .mbrAccesstoken(orginMember.getMbrAccesstoken())
                            .mbrRefreshtoken(orginMember.getMbrRefreshtoken())
                            .mbrSocialserver(orginMember.getMbrSocialserver())
                            .build();

            if(member.getMbrPwd()!= ""){
                setMember = setMember.toBuilder()
                        .mbrNick(member.getMbrNick())
                        .mbrUpdt(LocalDateTime.now())
                        .mbrEmail(member.getMbrEmail())
                        .mbrPwd(member.getMbrPwd())
                        .mbrId(member.getMbrId())
                        .build();

            }
            else{
                setMember = setMember.toBuilder()
                        .mbrNick(member.getMbrNick())
                        .mbrUpdt(LocalDateTime.now())
                        .mbrEmail(member.getMbrEmail())
                        .mbrId(member.getMbrId())
                        .build();
            }

            updateMem = memberRepository.save(setMember.ToEntity());
        }
        catch (Exception e){
            throw new SQLException(e);
        }
        //업데이트 후 바뀐 멤버 정보 세션에 담기
        MemberDTO dto= updateMem.ToDto();
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
