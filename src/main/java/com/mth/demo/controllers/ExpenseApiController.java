package com.mth.demo.controllers;

import com.mth.demo.exception.PfpException;
import com.mth.demo.exception.dataexportexceptions.DateRangeMissingException;
import com.mth.demo.exception.dataexportexceptions.DateRangeTooBigException;
import com.mth.demo.models.dto.finance.*;
import com.mth.demo.services.ExpenseService;

import java.io.IOException;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;

@RestController
@RequestMapping("/api/expense")
@RequiredArgsConstructor
public class ExpenseApiController {

    private final ExpenseService financeService;

    @PostMapping("/add")
    public ResponseEntity<ExpenseDTO> addNewExpense(@RequestParam(value = "json") String newExpense,
                                                    @RequestParam(required = false, value = "file") MultipartFile file,
                                                    @RequestHeader String authorization)
            throws PfpException, IOException {
        return ResponseEntity.ok().body(financeService.saveExpense(newExpense, file, authorization));
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<ExpenseDTO> updateExpense(@RequestParam(value = "json") String updateExpense,
                                                    @PathVariable Long id,
                                                    @RequestParam(required = false, value = "file") MultipartFile file,
                                                    @RequestHeader String authorization)
            throws PfpException, IOException {
        return ResponseEntity.ok()
                .body(financeService.updateExpense(updateExpense, id, file, authorization));
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<ExpenseDTO> getExpense(@PathVariable Long id,
                                                    @RequestHeader String authorization)
            throws PfpException {
        return ResponseEntity.ok().body(financeService.findById(id, authorization));
    }

    @GetMapping("/getAll")
    public ResponseEntity<GetAllExpenseDTO> getAllExpenses(@RequestHeader String authorization)
            throws PfpException {
        return ResponseEntity.ok().body(financeService.findAllByUser(authorization));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteExpense(@PathVariable Long id, @RequestHeader String authorization)
            throws PfpException {
        financeService.deleteFinanceById(id, authorization);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/countAll")
    public ResponseEntity<GetExpenseDTO> countAllExpenses(@RequestHeader String authorization)
            throws PfpException {
        return ResponseEntity.ok().body(financeService.countAllExpenses(authorization));
    }


    @GetMapping("/monthly")
    public ResponseEntity<List<MonthlyExpensesDTO>> getMonthlyExpenses(@RequestHeader String authorization, @RequestParam int monthOffset) {

        try {
            List<MonthlyExpensesDTO> expenseByMonth = financeService.getExpenseByMonth(authorization, monthOffset);
            return ResponseEntity.ok(expenseByMonth);
        } catch (DateRangeMissingException | DateRangeTooBigException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("/previous-and-current-month")
    public ResponseEntity<List<MonthlyExpensesDTO>> getMonthlyExpenses(
            @RequestHeader String authorization,
            @RequestParam(name = "monthType") String monthType
    ) {
        try {
            List<MonthlyExpensesDTO> expenses = financeService.getTotalExpenseForPreviousAndCurrentMonth(authorization, monthType);
            return ResponseEntity.ok(expenses);
        } catch (DateRangeMissingException | DateRangeTooBigException e) {
            return ResponseEntity.badRequest().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Collections.emptyList());
        } catch (PfpException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/each-month")
    public ResponseEntity<List<FinancesMonthlyDTO>> monthlyExpense(@RequestHeader String authorization)
            throws PfpException {
        return ResponseEntity.ok().body(financeService.monthlyExpense(authorization));
    }
}
