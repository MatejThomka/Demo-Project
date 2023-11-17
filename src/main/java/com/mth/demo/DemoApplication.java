package com.mth.demo;

import com.mth.demo.models.dto.authentication.RegisterDTO;
import com.mth.demo.models.entities.finance.Category;
import com.mth.demo.models.entities.finance.Finance;
import com.mth.demo.models.entities.finance.Goal;
import com.mth.demo.models.entities.finance.IncomeType;
import com.mth.demo.models.entities.user.User;
import com.mth.demo.repositories.CategoryRepository;
import com.mth.demo.repositories.FinanceRepository;
import com.mth.demo.repositories.GoalRepository;
import com.mth.demo.security.auth.AuthenticationService;
import com.mth.demo.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.Month;



@SpringBootApplication
@RequiredArgsConstructor
public class DemoApplication implements CommandLineRunner {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationService authService;
    private final FinanceRepository financeRepository;
    private final CategoryRepository categoryRepository;
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

        Category category = new Category(-1L, "transfer");
        categoryRepository.save(category);
        category = new Category(-1L, "food");
        categoryRepository.save(category);
        category = new Category(-1L, "clothes");
        categoryRepository.save(category);
        category = new Category(-1L, "entertainment");
        categoryRepository.save(category);
        category = new Category(-1L, "transportation");
        categoryRepository.save(category);
        Category categoryOther = new Category(-1L, "other");
        categoryRepository.save(categoryOther);

        Finance finance1 = new Finance();
        finance1.setUser(userService.findById(1L));
        finance1.setIncome(100.);
        finance1.setRecurring(true);
        finance1.setIncomeType(IncomeType.WORK);
        finance1.setExpense(0.0);
        finance1.setCategory(categoryOther);
        finance1.setTransactionDate(LocalDate.of(2023, Month.FEBRUARY, 2));
        financeRepository.save(finance1);

        Finance finance2 = new Finance();
        finance2.setUser(userService.findById(1L));
        finance2.setIncome(155.5);
        finance2.setRecurring(true);
        finance2.setIncomeType(IncomeType.WORK2);
        finance2.setExpense(0.0);
        finance2.setCategory(categoryOther);
        finance2.setTransactionDate(LocalDate.of(2023, Month.APRIL, 21));
        financeRepository.save(finance2);

        Finance finance3 = new Finance();
        finance3.setUser(userService.findById(3L));
        finance3.setIncome(50.);
        finance3.setRecurring(false);
        finance3.setIncomeType(IncomeType.HERITAGE);
        finance3.setExpense(0.0);
        finance3.setCategory(categoryOther);
        finance3.setTransactionDate(LocalDate.of(2023, Month.JANUARY, 1));
        financeRepository.save(finance3);

        Finance finance4 = new Finance();
        finance4.setUser(userService.findById(2L));
        finance4.setIncome(2000.5);
        finance4.setRecurring(false);
        finance4.setIncomeType(IncomeType.WORK2);
        finance4.setExpense(0.0);
        finance4.setCategory(categoryOther);
        finance4.setTransactionDate(LocalDate.of(2020, 9, 20));
        financeRepository.save(finance4);

        Finance finance5 = new Finance();
        finance5.setUser(userService.findById(2L));
        finance5.setIncome(2100.4);
        finance5.setRecurring(true);
        finance5.setIncomeType(IncomeType.WORK);
        finance5.setExpense(550.5);
        finance5.setCategory(categoryOther);
        finance5.setTransactionDate(LocalDate.of(2023, 1, 15));
        financeRepository.save(finance5);

        Finance finance6 = new Finance();
        finance6.setUser(userService.findById(2L));
        finance6.setIncome(2100.4);
        finance6.setRecurring(true);
        finance6.setIncomeType(IncomeType.WORK);
        finance6.setExpense(250.5);
        finance6.setCategory(categoryOther);
        finance6.setTransactionDate(LocalDate.of(2023, 2, 15));
        financeRepository.save(finance6);

        Finance finance7 = new Finance();
        finance7.setUser(userService.findById(2L));
        finance7.setIncome(2500.4);
        finance7.setRecurring(true);
        finance7.setIncomeType(IncomeType.WORK);
        finance7.setExpense(220.5);
        finance7.setCategory(categoryOther);
        finance7.setTransactionDate(LocalDate.of(2023, 3, 15));
        financeRepository.save(finance7);

        Finance finance8 = new Finance();
        finance8.setUser(userService.findById(2L));
        finance8.setIncome(3100.4);
        finance8.setRecurring(true);
        finance8.setIncomeType(IncomeType.WORK);
        finance8.setExpense(150.5);
        finance8.setCategory(categoryOther);
        finance8.setTransactionDate(LocalDate.of(2023, 4, 15));
        financeRepository.save(finance8);

        Finance finance9 = new Finance();
        finance9.setUser(userService.findById(2L));
        finance9.setIncome(2100.4);
        finance9.setRecurring(true);
        finance9.setIncomeType(IncomeType.WORK);
        finance9.setExpense(550.5);
        finance9.setCategory(categoryOther);
        finance9.setTransactionDate(LocalDate.of(2023, 5, 15));
        financeRepository.save(finance9);

        Finance finance10 = new Finance();
        finance10.setUser(userService.findById(2L));
        finance10.setIncome(4500.4);
        finance10.setRecurring(true);
        finance10.setIncomeType(IncomeType.WORK);
        finance10.setExpense(1100.5);
        finance10.setCategory(categoryOther);
        finance10.setTransactionDate(LocalDate.of(2023, 6, 15));
        financeRepository.save(finance10);

        Finance finance11 = new Finance();
        finance11.setUser(userService.findById(2L));
        finance11.setIncome(5100.4);
        finance11.setRecurring(true);
        finance11.setIncomeType(IncomeType.WORK);
        finance11.setExpense(1450.5);
        finance11.setCategory(categoryOther);
        finance11.setTransactionDate(LocalDate.of(2023, 7, 15));
        financeRepository.save(finance11);

        Finance finance12 = new Finance();
        finance12.setUser(userService.findById(2L));
        finance12.setIncome(17500.4);
        finance12.setRecurring(true);
        finance12.setIncomeType(IncomeType.WORK);
        finance12.setExpense(7250.5);
        finance12.setCategory(categoryOther);
        finance12.setTransactionDate(LocalDate.of(2023, 8, 15));
        financeRepository.save(finance12);

        Finance finance13 = new Finance();
        finance13.setUser(userService.findById(2L));
        finance13.setIncome(2100.4);
        finance13.setRecurring(true);
        finance13.setIncomeType(IncomeType.WORK);
        finance13.setExpense(670.5);
        finance13.setCategory(categoryOther);
        finance13.setTransactionDate(LocalDate.of(2023, 9, 15));
        financeRepository.save(finance13);


        Finance finance14 = new Finance();
        finance14.setUser(userService.findById(2L));
        finance14.setIncome(3500.4);
        finance14.setRecurring(true);
        finance14.setIncomeType(IncomeType.WORK);
        finance14.setExpense(550.5);
        finance14.setCategory(categoryOther);
        finance14.setTransactionDate(LocalDate.of(2023, 10, 15));
        financeRepository.save(finance14);

        Finance finance15 = new Finance();
        finance15.setUser(userService.findById(2L));
        finance15.setIncome(3100.4);
        finance15.setRecurring(true);
        finance15.setIncomeType(IncomeType.WORK);
        finance15.setExpense(750.5);
        finance15.setCategory(categoryOther);
        finance15.setTransactionDate(LocalDate.of(2023, 11, 15));
        financeRepository.save(finance15);

        Finance finance16 = new Finance();
        finance16.setUser(userService.findById(2L));
        finance16.setIncome(18100.4);
        finance16.setRecurring(true);
        finance16.setIncomeType(IncomeType.WORK);
        finance16.setExpense(8450.5);
        finance16.setCategory(categoryOther);
        finance16.setTransactionDate(LocalDate.of(2023, 12, 15));
        financeRepository.save(finance16);

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