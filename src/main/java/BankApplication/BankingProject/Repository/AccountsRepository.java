package BankApplication.BankingProject.Repository;

import BankApplication.BankingProject.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountsRepository extends JpaRepository<User,Integer> {
    User findByEmail(String email);
     User findByAccnoAndPin(Long accno, int pin);
    User findByAccno(Long accno);


}
