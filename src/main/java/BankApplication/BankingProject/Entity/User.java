package BankApplication.BankingProject.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Table(name="accounts")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name="accno")
    Long accno;

    @Column(name = "name")
    String name;

    @Column(name = "bal")
    Long bal;

    @Column(name = "pin")
    int pin;

    @Column(name = "email")
    String email;


}
