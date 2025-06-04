package BankApplication.BankingProject.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendOtpEmail(String email, String otpStr) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("patiluday7122003@gmail.com");
        message.setTo(email);
        message.setText(otpStr);
        message.setSubject("Your OTP Code");
        mailSender.send(message);

    }
}
