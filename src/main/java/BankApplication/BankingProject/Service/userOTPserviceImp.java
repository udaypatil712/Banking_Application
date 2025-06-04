package BankApplication.BankingProject.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class userOTPserviceImp implements userOTPservice{



    @Autowired
    private EmailService emailService;

    public String generateOpt(String email) {
        int otp = (int) (Math.random() * 900000) + 100000;
        String otpStr = String.valueOf(otp);

        // Send the OTP via email
        emailService.sendOtpEmail(email, otpStr);

        return otpStr;
    }
}
