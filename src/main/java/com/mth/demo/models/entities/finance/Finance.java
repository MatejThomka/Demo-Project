package com.mth.demo.models.entities.finance;

import com.mth.demo.models.entities.user.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "finances")
public class Finance implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    Long id;

    @Column(name = "income")
    Double income;

    @Column(name = "expense")
    Double expense;

    @Column(name = "reccuring")
    Boolean recurring;

    @Column(name = "transaction_date", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDate transactionDate;

    @Column(name = "income_type")
    IncomeType incomeType;

    @ManyToOne
    Category category;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    @Column(name = "file_name")
    String fileName;

    @Column(name = "file_type")
    String fileType;

    @Lob
    @Column(name = "data", columnDefinition = "LONGBLOB")
    byte[] fileData;

    public Finance(Double income, Category category, Boolean recurring, LocalDate transactionDate,
                   IncomeType incomeType, User user) {
        this.income = income;
        this.expense = 0.0;
        this.category = category;
        this.recurring = recurring;
        this.transactionDate = transactionDate;
        this.incomeType = incomeType;
        this.user = user;
    }

    public Finance(Double expense, Category category, LocalDate now, User user) {
        this.expense = expense;
        this.income = 0.0;
        this.incomeType = IncomeType.OTHER;
        this.category = category;
        this.transactionDate = now;
        this.recurring = false;
        setUser(user);
    }

    public Finance(long id, double expense, Category category, LocalDate localDate, User user) {
        this.id = id;
        this.expense = expense;
        this.income = 0.0;
        this.incomeType = IncomeType.OTHER;
        this.category = category;
        this.transactionDate = localDate;
        this.recurring = false;
        setUser(user);
    }

    public Finance(long financeId, double income, Category category, boolean recurring, LocalDate transactionDate, IncomeType incomeType, User user) {
        this.id = financeId;
        this.income = income;
        this.category = category;
        this.recurring = recurring;
        this.transactionDate = transactionDate;
        this.incomeType = incomeType;
        this.user = user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}