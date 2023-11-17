package com.mth.demo.services;

import com.mth.demo.exception.PfpException;
import com.mth.demo.exception.dataexportexceptions.DateRangeMissingException;
import com.mth.demo.exception.dataexportexceptions.DateRangeTooBigException;
import com.mth.demo.models.dto.finance.*;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public interface ExpenseService {
    ExpenseDTO saveExpense(String newExpense, MultipartFile file, String request) throws PfpException, IOException;

    ExpenseDTO updateExpense(String updateExpense, Long id, MultipartFile file, String request) throws PfpException, IOException;

    ExpenseDTO findById(Long id, String request) throws PfpException;

    GetAllExpenseDTO findAllByUser(String request) throws PfpException;

    void deleteFinanceById(Long id, String request) throws PfpException;

    double getTotalExpenseBetweenDates(String request, LocalDate startDate, LocalDate endDate) throws DateRangeMissingException, DateRangeTooBigException;

    List<MonthlyExpensesDTO> getTotalExpenseForPreviousAndCurrentMonth(String request, String monthType) throws PfpException;

    List<MonthlyExpensesDTO> getExpenseByMonth(String request, int monthOffset) throws DateRangeMissingException, DateRangeTooBigException;

    GetExpenseDTO countAllExpenses(String request) throws PfpException;

    List<FinancesMonthlyDTO> monthlyExpense(String request) throws PfpException;
}