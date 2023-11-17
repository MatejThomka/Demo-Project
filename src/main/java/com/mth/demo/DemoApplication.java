package com.mth.demo;

import com.mth.demo.models.dto.authentication.RegisterDTO;
import com.mth.demo.models.entities.finance.Goal;
import com.mth.demo.models.entities.user.User;
import com.mth.demo.repositories.GoalRepository;
import com.mth.demo.security.auth.AuthenticationService;
import com.mth.demo.services.UserService;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;



@SpringBootApplication
@RequiredArgsConstructor
public class DemoApplication implements CommandLineRunner {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationService authService;
    private final GoalRepository goalRepository;

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        authService.createUser(new RegisterDTO("Luke", "Skywalker", "luke@gmail.com", "123456"));
        authService.createUser(new RegisterDTO("Pumba", "Warthog", "pumba@gmail.com", "123456"));
        authService.createUser(new RegisterDTO("Harry", "Potter", "harry@gmail.com", "123456"));

        LocalDate joinDate = LocalDate.of(2021, 1, 1);
        User admin = new User("Admin", "One", "admin@gmail.com", passwordEncoder.encode("654321"), joinDate, "ADMIN");
        userService.updateUser(admin);

        Goal goal = new Goal();
        goal.setName("For a Car");
        goal.setTargetGoal(17000.00);
        goal.setDeposit(150.00);
        goal.setWithdrawal(100.00);
        goal.setCurrentBalance(6400.00);
        goal.setUser(userService.findById(2L));
        goalRepository.save(goal);

        Goal goal1 = new Goal();
        goal1.setName("For a House");
        goal1.setTargetGoal(50000.00);
        goal1.setDeposit(1500.00);
        goal1.setWithdrawal(100.00);
        goal1.setCurrentBalance(15400.00);
        goal1.setUser(userService.findById(2L));
        goalRepository.save(goal1);

        Goal goal2 = new Goal();
        goal2.setName("For a Baby");
        goal2.setTargetGoal(5000.00);
        goal2.setDeposit(250.00);
        goal2.setWithdrawal(0.00);
        goal2.setCurrentBalance(1400.00);
        goal2.setUser(userService.findById(2L));
        goalRepository.save(goal2);

    }
}