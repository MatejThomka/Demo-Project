package com.mth.demo.repositories;

import com.mth.demo.models.entities.finance.Finance;
import com.mth.demo.models.entities.finance.IncomeType;
import com.mth.demo.models.entities.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public interface FinanceRepository extends JpaRepository<Finance, Long> {
  @Override
  Optional<Finance> findById(Long id);

  Optional<Finance> findByIdAndUser(Long id, User user);

  ArrayList<Finance> findAllByUser(User user);

  ArrayList<Finance> findAllByUserAndExpenseNotNullOrderByTransactionDate(User user);

  ArrayList<Finance> findAllByUserAndIncomeNotNullOrderByTransactionDate(User user);

  Optional<Finance> findFirstByUserAndRecurringAndIncomeTypeAndIncomeNotNullOrderByTransactionDateDesc(
      User user, Boolean recurring, IncomeType incomeType);

  Optional<Finance> findByUserAndIncomeTypeAndTransactionDate(User user, IncomeType incomeType,
                                                              LocalDate date);

  boolean existsByIdAndUser(Long id, User user);

  void deleteById(Long id);

  List<Finance> findByUserIdAndTransactionDateBetween(Long userId, LocalDate startDate,
                                                      LocalDate endDate);

  List<Finance> findByUserAndTransactionDateBetween(User user, LocalDate startDate,
                                                    LocalDate endDate);

  List<Finance> findByUserIdAndCategoryIdAndTransactionDateBetween(Long userId, Long categoryId, LocalDate startDate, LocalDate endDate);
}