package com.mefi.backend.api.service;

import com.mefi.backend.api.request.VerifyCodeReqDto;
import com.mefi.backend.common.exception.ErrorCode;
import com.mefi.backend.common.exception.Exceptions;
import com.mefi.backend.db.entity.EmailAuth;
import com.mefi.backend.db.repository.MailRepository;
import com.mefi.backend.db.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

import static com.mefi.backend.common.exception.ErrorCode.CODE_NOT_MATCH;

@Slf4j
@RequiredArgsConstructor
@Service
public class MailServiceImpl implements MailService {
    private final JavaMailSender javaMailSender;
    private final MailRepository mailRepository;
    private final UserRepository userRepository;

    // 이메일
    @Value("${spring.mail.username}")
    private String email;

    // 인증 코드 생성
    public String createAuthCode() {

        // 인증 코드 저장 버퍼
        StringBuffer sb = new StringBuffer();

        // 랜덤 객체 생성
        Random random = new Random();

        // 인증코드 6자리 생성
        for(int i=0; i<6; i++)
            sb.append(random.nextInt(10));

        return sb.toString();
    }

    // 메일 내용 생성
    public MimeMessage createMessage(String receiver, String authCode) throws MessagingException, UnsupportedEncodingException {

        // 메일 내용 객체 생성
        MimeMessage message = javaMailSender.createMimeMessage();

        // 메일 제목
        message.setSubject("MEFI 인증 코드 안내");

        // 받는 사람 지정
        message.addRecipients(MimeMessage.RecipientType.TO, receiver);

        // 메일 내용 (SubType HTML 지정)
        String msg =
                "<h1 style=\"font-size: 30px; padding: 15px 10px;\">MEFI 인증 코드 안내드립니다.</h1>"+
                        "<p style=\"font-size: 17px; padding: 10px 10px;\">" +
                        "아래 인증 코드를 입력해주세요.</p>"+
                        "<p style=\"font-size: 17px; padding: 10px 10px;\">" +
                        "저희 MEFI를 이용해주셔서 감사합니다.</p>"+
                        "<p style=\"font-size: 40px; font-weight: bold; padding: 10px 10px;\">" +
                        authCode+"</p>";

        // 메일 내용 설정 (내용, Charset, SubType)
        message.setText(msg, "utf-8", "html");

        // 발신자 정보 설정 (발신자, 보내는 사람 이름)
        // 예) 보내는 사람 이름 <mefi_undefined@naver.com>
        message.setFrom(new InternetAddress(email,"MEFI"));

        return message;
    }

    // 회원가입을 위한 메일 전송
    @Transactional
    public void sendJoinMessage(String email) throws MessagingException, UnsupportedEncodingException {

        // 이메일 중복 검사
        if(userRepository.findByEmail(email).isPresent())
            throw new Exceptions(ErrorCode.EMAIL_EXIST);

        // 인증 코드 생성
        String authCode = createAuthCode();

        // 메일 내용 객체 생성
        MimeMessage message = createMessage(email, authCode);

        // 이메일로 인증 엔티티 조회
        Optional<EmailAuth> auth = mailRepository.findByEmail(email);

        // 이미 인증 코드가 존재하는 경우 (인증 코드 업데이트)
        if (auth.isPresent()) {
            auth.get().updateAuthCode(authCode);

            // DB 저장
            mailRepository.save(auth.get());
        }

        // 인증 코드가 존재하지 않는 경우 (인증 코드 엔티티 생성)
        else {
            EmailAuth emailAuth = EmailAuth.builder()
                    .email(email)
                    .randomNum(authCode)
                    .build();

            // DB 저장
            mailRepository.save(emailAuth);
        }

        // 메일 전송
        javaMailSender.send(message);
    }

    // 비밀번호를 찾기 위한 메일 전송
    @Override
    public void sendPasswordRecoveryMessage(String email) throws MessagingException, UnsupportedEncodingException {

        // 가입된 이메일인지 확인
        if(!userRepository.findByEmail(email).isPresent())
            throw new Exceptions(ErrorCode.EMAIL_NOT_EXIST);

        // 인증 코드 생성
        String authCode = createAuthCode();

        // 메일 내용 객체 생성
        MimeMessage message = createMessage(email, authCode);

        // 이메일로 인증 엔티티 조회
        Optional<EmailAuth> auth = mailRepository.findByEmail(email);

        // 이미 인증 코드가 존재하는 경우 (인증 코드 업데이트)
        if (auth.isPresent()) {
            auth.get().updateAuthCode(authCode);

            // DB 저장
            mailRepository.save(auth.get());
        }

        // 인증 코드가 존재하지 않는 경우 (인증 코드 엔티티 생성)
        else {
            EmailAuth emailAuth = EmailAuth.builder()
                    .email(email)
                    .randomNum(authCode)
                    .build();

            // DB 저장
            mailRepository.save(emailAuth);
        }

        // 메일 전송
        javaMailSender.send(message);
    }

    // 인증 코드 확인
    @Override
    public void validateAuthCode(VerifyCodeReqDto verifyCodeReqDto) {

        // 인증 코드 유효성 검사

        // 인증하려는 이메일에 인증 메일이 미전송했을 경우
        if(!mailRepository.findByEmail(verifyCodeReqDto.getEmail()).isPresent())
            throw new Exceptions(ErrorCode.CODE_NOT_EXIST);

        // 이메일로 인증 엔티티 조회
        EmailAuth emailAuth = mailRepository.findByEmail(verifyCodeReqDto.getEmail()).get();

        // 인증 시간 만료 여부
        if(emailAuth.getCreatedTime().plusMinutes(3)
                .isBefore(LocalDateTime.now()))
            throw new Exceptions(ErrorCode.CODE_TIME_EXPIRED);

        // 인증 코드 일치 여부
        if(!emailAuth.getRandomNum().equals(verifyCodeReqDto.getAuthCode()))
            throw new Exceptions(CODE_NOT_MATCH);

        // 토큰 발행 (주석 이유 : 추가 로직 필요!)
        // return jwtUtil.createJwt(emailAuth.getEmail(), "undefined", 60*3*1000L);
    }
}