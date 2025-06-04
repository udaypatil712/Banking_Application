package BankApplication.BankingProject.Controller;

import BankApplication.BankingProject.Entity.User;
import BankApplication.BankingProject.Repository.AccountsRepository;
import BankApplication.BankingProject.Service.EmailService;
import BankApplication.BankingProject.Service.userOTPservice;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class UserController {

    @Autowired
    private AccountsRepository accountsRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private userOTPservice userOtpService; // ✅ renamed to match Java convention

    private String generatedOtp;
    private String userEmail;

    @GetMapping("/Home")
    public String home() {
        return "Home";
    }

    @GetMapping("/register")
    public String register() {
        return "Register";
    }

    @PostMapping("/save")
    public String saveAccount(@ModelAttribute User user, Model model) {
        accountsRepository.save(user);
        model.addAttribute("message", "Account has been created successfully.");
        return "Login";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "Login";
    }

    // Step 1: Send OTP
    @PostMapping("/login/otp")
    public String sendOtp(@RequestParam String email, Model model) {
        User user = accountsRepository.findByEmail(email);
        if (user != null) {
            userEmail = user.getEmail();
            generatedOtp = userOtpService.generateOpt(email);
            System.out.println(generatedOtp);
            model.addAttribute("email", userEmail);
            model.addAttribute("message", "OTP sent to your email.");
            model.addAttribute("showOtpForm", true);
        } else {
            model.addAttribute("error", "Invalid email.");
        }
        return "Login";
    }


    @PostMapping("/login/user")
    public String verifyOtp(@RequestParam String email,
                            @RequestParam String otp,
                            Model model) {

        System.out.println(email);
        System.out.println(otp);
        if (email.equals(userEmail) && otp.equals(generatedOtp)) {
            model.addAttribute("message", "Login successful.");
            return "Welcome";
        } else {
            model.addAttribute("email", email);
            model.addAttribute("error", "Invalid OTP.");
            model.addAttribute("showOtpForm", true);
            return "Login";
        }
    }

    @GetMapping("/deposit")
    public String deposit(){
        return "Deposit";
    }

    @PostMapping("/deposit/amount")
    public String doDeposit(@RequestParam long accno,
                            @RequestParam int pin,
                            @RequestParam long bal,
                            Model model){

        System.out.println("Deposit amount.."+bal);
        User user=accountsRepository.findByAccnoAndPin(accno,pin);
        if(user != null){
            Long currentBal=user.getBal();
            user.setBal(currentBal + bal);
            accountsRepository.save(user);
            System.out.println("Amount is deposited...");
            return "Welcome";
        }else {
            System.out.println("amount is not Deposited..");
            return "Deposit";
        }
    }

    @GetMapping("/withdraw")
    public String withdraw(){
        return "Withdraw";
    }

    @PostMapping("/withdraw/otp")
    public String withdrawOtp(@RequestParam long accno,
                              @RequestParam int pin,
                              @RequestParam long bal,
                              @RequestParam String email,
                              Model model,
                              HttpSession session) {

        // 1. Find user by accno and pin
        User user = accountsRepository.findByAccnoAndPin(accno, pin);

        if (user != null) {
            // 2. Get registered email from user record
            String registeredEmail = user.getEmail();
            Long currentBal = user.getBal();

            // 3. Check if balance is sufficient
            if (currentBal >= bal) {
                // 4. Check if input email matches registered email
                if (!email.equals(registeredEmail)) {
                    model.addAttribute("error", "Entered email does not match registered email.");
                    return "Withdraw";
                }

                if (bal >= 1000000) {
                    // 5. For large amount, send OTP
                    session.setAttribute("accno", accno);
                    session.setAttribute("pin", pin);
                    session.setAttribute("bal", bal);
                    session.setAttribute("email", email);

                    String generatedOtp = userOtpService.generateOpt(email);
                    session.setAttribute("generatedOtp", generatedOtp);

                    System.out.println("Otp for Withdraw: " + generatedOtp);
                    model.addAttribute("email", email);
                    model.addAttribute("showOtpForm", true);
                    return "Withdraw";
                } else {
                    // 6. Withdraw directly
                    user.setBal(currentBal - bal);
                    accountsRepository.save(user);
                    model.addAttribute("msg", "Amount withdrawn successfully.");
                    return "Withdraw";
                }
            } else {
                model.addAttribute("errorfund", "Insufficient funds.");
                return "Withdraw";
            }
        } else {
            model.addAttribute("error", "Invalid account number or PIN.");
            return "Withdraw";
        }
    }

    @PostMapping("/withdraw/user")
    public String verifyOtp(@RequestParam String email,
                            @RequestParam String otp,
                            Model model,
                            HttpSession session) {

        String sessionEmail = (String) session.getAttribute("email");
        String sessionOtp = (String) session.getAttribute("generatedOtp");
        Long accno = (Long) session.getAttribute("accno");
        Integer pin = (Integer) session.getAttribute("pin");
        Long bal = (Long) session.getAttribute("bal");

        if (email.equals(sessionEmail) && otp.equals(sessionOtp)) {
            User user = accountsRepository.findByAccnoAndPin(accno, pin);
            if (user != null && user.getBal() >= bal) {
                user.setBal(user.getBal() - bal);
                accountsRepository.save(user);
                model.addAttribute("msg", "Withdraw successful.");
            } else {
                model.addAttribute("errorfund", "Insufficient funds.");
            }
            session.invalidate(); // clear session data
            return "Withdraw";
        } else {
            model.addAttribute("email", email);
            model.addAttribute("error", "Invalid OTP.");
            model.addAttribute("showOtpForm", true);
            return "Withdraw";
        }
    }




    @GetMapping("/checkBalance")
    public String checkBalance(){
        return "CheckBalance";
    }

    @PostMapping("/check/Balance")
    public String doCheckBalance(@RequestParam long accno,
                                 @RequestParam int pin,
                                 Model model){

        User user=accountsRepository.findByAccnoAndPin(accno,pin);
        if (user != null){
            long checkbalance=user.getBal();
            System.out.println("your Balance.."+checkbalance);
            model.addAttribute("check","₹"+checkbalance);
            return "ShowBalance";
        }else{
            model.addAttribute("error","check Information...");
            return "CheckBalance";
        }

    }

    @GetMapping("/done")
    public String done(){
        return "Welcome";
    }

    @GetMapping("/transfer")
    public String transfer(){
        return "TransferMoney";
    }

    @PostMapping("/transfer/money")
    public String doTransfer(@RequestParam long accno,         // Sender's acc no
                             @RequestParam int pin,            // Sender's pin
                             @RequestParam long accno1,        // Receiver's acc no
                             @RequestParam long bal,        // Amount to transfer
                             Model model) {

        User send=accountsRepository.findByAccnoAndPin(accno,pin);
        User receive=accountsRepository.findByAccno(accno1);
        System.out.println(receive);
        if ( send != null){
            long currentAmount=send.getBal();
            System.out.println(currentAmount);
            if (currentAmount > bal){
                send.setBal(send.getBal() - bal);
                receive.setBal(receive.getBal() + bal);
                accountsRepository.save(send);
                accountsRepository.save(receive);
                System.out.println("money transfer succesfully...");
                return "Welcome";
            }else {
                System.out.println("insuffient fund..");
                return "TransferMoney";
            }

        }
        else {
            System.out.println("fill proper information about you..");
            return "TransferMoney";
        }

    }


}