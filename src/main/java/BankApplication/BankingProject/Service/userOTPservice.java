package BankApplication.BankingProject.Service;

import org.springframework.stereotype.Service;

@Service
public interface userOTPservice {

        String generateOpt(String email);
}
