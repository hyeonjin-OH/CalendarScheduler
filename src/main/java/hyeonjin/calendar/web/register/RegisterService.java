package hyeonjin.calendar.web.register;


import hyeonjin.calendar.domain.category.CalCategory;
import hyeonjin.calendar.domain.category.CategoryRepository;
import hyeonjin.calendar.domain.member.Member;
import hyeonjin.calendar.domain.member.MembersRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class RegisterService {

    private final MembersRepository memberRepository;

    private final CategoryRepository categoryRepository;
    @Transactional(rollbackOn = {SQLException.class})
    public String register(Member member) throws SQLException{

        Long seq = memberRepository.countBy();

        LocalDateTime nowT = LocalDateTime.now();
        if(member.getMbrSeqn()== null){
            member.setMbrSeqn(Long.parseLong(DateTimeFormatter.ofPattern("HHmmss").format(nowT) + (++seq).toString()));
        }

        member.setMbrRgdt(nowT);
        try{
            Member newMember = memberRepository.save(member);

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

        return "redirect:/login";
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
    public String updatePwd(Member member) throws SQLException{

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

        return "redirect:/login";
    }

    @Transactional(rollbackOn = {SQLException.class})
    public String updateInfo(Member member) throws SQLException{

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

            memberRepository.save(orginMember);
        }
        catch (Exception e){
            throw new SQLException(e);
        }

        return "redirect:/Calendar";
    }
}
