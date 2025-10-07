package com.mimawiki.api.service;

import com.mimawiki.api.dto.req.MemberSignupRequestDto;
import com.mimawiki.api.entity.Member;
import com.mimawiki.api.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class MemberService {
    //add to repository static variables;
    private final MemberRepository memberRepository;

    private final PasswordEncoder passwordEncoder;

//    /**
//     * 회원정보 조회
//     * @param email
//     * @return
//     */
//    public MemberInfoRes findByUserId(String email) {
//        Optional<Member> memberOptional = memberRepository.findByEmail(email);
//        return memberOptional
//                .map(member -> MemberInfoRes.builder()
//                        .id(member.getId())
//                        .email(member.getEmail())
//                        .name(member.getName())
//                        .build())
//                .orElseThrow(() -> new NoSuchElementException("해당 이메일의 사용자를 찾을 수 없습니다."));
//    }


    /**
     * 회원가입
     * @param dto
     * @return
     */
    public boolean signup(MemberSignupRequestDto dto) {
        Optional<Member> memberOptional = memberRepository.findByEmail(dto.getEmail());
        //이메일 중복체크
        if (memberOptional.isPresent()) {
            throw new IllegalStateException("이미 존재하는 이메일입니다.");
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(dto.getPasswd());
        dto.setPasswd(encodedPassword);

        Member member = dto.toEntity();
        log.info("member: {}", member.getName());
        memberRepository.save(member);
        return true;
    }

//    public Optional<MemberProfileRequestDto> profile(String email) {
//        return memberRepository.findByEmail(email)
//                .map(MemberProfileRequestDto::UserDto);
//    }


    /**
     * 회원아이디(이메일)로 Member Entity 리턴
     * @param email
     * @return
     */
    public Member getMemberByEmail(String email) {
        return memberRepository.findByEmail(email).orElseThrow();
    }

//    public PatchProfileRes patchProfile(String email, PatchProfileReqDto dto) {
//        Optional<Member> optionalMember = memberRepository.findByEmail(email);
//
//        // 회원이 없을 경우 false 리턴
//        if (optionalMember.isEmpty()) {
//            return PatchProfileRes.builder()
//                    .success(false)
//                    .build();
//        }
//
//        // 이메일로 회원 조회
//        Member member = optionalMember.get();
//
//        // 이름이 없을 경우 false 리턴
//        if (dto.getName() == null || dto.getName().isBlank()) {
//        return PatchProfileRes.builder()
//                .success(false)
//                .build();
//
//        }
//
//        member.setName(dto.getName());
//
//        // dto에 있는 값을 확인하여 member 업데이트
//        if (dto.getProfileImage() != null) {
//            member.setProfileImage(dto.getProfileImage());
//        }
//
//        if (dto.getCoverColor() != null) {
//            member.setCoverColor(dto.getCoverColor());
//        }
//
//        if (dto.getQuoteTitle() != null) {
//            member.setQuoteTitle(dto.getQuoteTitle());
//        }
//
//        if (dto.getQuoteText() != null) {
//            member.setQuoteText(dto.getQuoteText());
//        }
//
//        // 음악 정보는 중첩 객체이므로 이중 확인
//        if (dto.getMusic() != null) {
//            MusicReqDto musicDto = dto.getMusic();
//            Music music = new Music();
//
//            if (musicDto.getId() != null) music.setId(musicDto.getId());
//            if (musicDto.getSong() != null) music.setSong(musicDto.getSong());
//            if (musicDto.getArtist() != null) music.setArtist(musicDto.getArtist());
//
//            member.setMusic(music);
//        }
//
//        // 수정된 프로필 정보 저장
//        memberRepository.save(member);
//
//        // 성공시 true 리턴
//        return PatchProfileRes.builder()
//                .success(true)
//                .build();
//    }

}
