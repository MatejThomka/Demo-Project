package com.mth.demo.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mth.demo.exception.NothingInsideException;
import com.mth.demo.exception.PfpException;
import com.mth.demo.exception.UnauthorizedException;
import com.mth.demo.exception.dataexportexceptions.DateRangeMissingException;
import com.mth.demo.exception.dataexportexceptions.DateRangeTooBigException;
import com.mth.demo.exception.financeexceptions.FinanceNotFoundException;
import com.mth.demo.models.dto.finance.ExpenseDTO;
import com.mth.demo.models.dto.finance.FinancesMonthlyDTO;
import com.mth.demo.models.dto.finance.GetAllExpenseDTO;
import com.mth.demo.models.dto.finance.GetExpenseDTO;
import com.mth.demo.models.dto.finance.MonthlyExpensesDTO;
import com.mth.demo.models.entities.finance.Category;
import com.mth.demo.models.entities.finance.Finance;
import com.mth.demo.models.entities.user.User;
import com.mth.demo.repositories.CategoryRepository;
import com.mth.demo.repositories.FinanceRepository;
import com.mth.demo.repositories.UserRepository;
import com.mth.demo.security.config.JwtService;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ExpenseServiceImpl implements ExpenseService {

    private final FinanceRepository financeRepository;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ObjectMapper objectMapper;

    @Override
    public ExpenseDTO saveExpense(String newExpense, MultipartFile file, String request) throws FinanceNotFoundException, IOException {
        User user = userRepository.findByEmailOrderById(jwtService.extractUserNameFromHeader(request));

        Finance finance = objectMapper.readValue(newExpense, Finance.class);
        String categoryName = finance.getCategory().getName();
        if (categoryName == null || categoryName.trim().isEmpty()) {
            categoryName = "other";
        }
        Category category = categoryRepository.findByName(categoryName)
                .orElseThrow(() -> new FinanceNotFoundException("Category '" + finance.getCategory().getName() + "' not found"));

        LocalDate transactionDate = (finance.getTransactionDate() != null) ? finance.getTransactionDate() : LocalDate.now();

        Finance expense = new Finance(finance.getExpense(), category, transactionDate, user);
        String fileResponse = "No file uploaded";

        if (file != null && !file.isEmpty()) {
            if (file.getSize() > 4 * 1024 * 1024) {
                throw new IOException("File is too big");
            }
            expense.setFileName(file.getOriginalFilename());
            expense.setFileType(file.getContentType());
            expense.setFileData(file.getBytes());
            fileResponse = file.getOriginalFilename();
        }

        financeRepository.save(expense);

        return ExpenseDTO.builder()
                .expense(expense.getExpense())
                .categoryName(expense.getCategory().getName())
                .transactionDate(transactionDate)
                .fileName(fileResponse)
                .build();
    }

    @Override
    public ExpenseDTO updateExpense(String updateExpense, Long id, MultipartFile file, String request) throws PfpException, IOException {
        User user = userRepository.findByEmailOrderById(jwtService.extractUserNameFromHeader(request));

        Finance finance = objectMapper.readValue(updateExpense, Finance.class);

        String categoryName = finance.getCategory().getName();

        Finance financeToUpdate = financeRepository.findById(id)
                .orElseThrow(() -> new FinanceNotFoundException("Finance with ID " + id + " not found"));

        if (categoryName == null || categoryName.trim().isEmpty() || categoryName.equals("null")) {
            categoryName = financeToUpdate.getCategory().getName();
        }
        Category category = categoryRepository.findByName(categoryName)
                .orElseThrow(() -> new FinanceNotFoundException("Category not found"));

        if (!financeRepository.existsByIdAndUser(id, user)) {
            throw new UnauthorizedException("You are not authorized to update this finance");
        }

        if (finance.getExpense() == null || finance.getExpense().equals("")) {
            finance.setExpense(financeToUpdate.getExpense());
        }

        if (finance.getCategory() == null || finance.getCategory().equals("")) {
            finance.setCategory(category);
        }

        if (finance.getTransactionDate() == null || finance.getTransactionDate().equals("")) {
            finance.setTransactionDate(financeToUpdate.getTransactionDate());
        }

        String fileResponse = "No file uploaded";

        if (file != null && !file.isEmpty()) {
            if (file.getSize() > 4 * 1024 * 1024) {
                throw new IOException("File is too big");
            }
            financeToUpdate.setFileName(file.getOriginalFilename());
            financeToUpdate.setFileType(file.getContentType());
            financeToUpdate.setFileData(file.getBytes());
            fileResponse = file.getOriginalFilename();
        } else if (financeToUpdate.getFileName() != null && !financeToUpdate.getFileName().isEmpty()) {
            fileResponse = financeToUpdate.getFileName();
        }

        financeToUpdate.setExpense(finance.getExpense());
        financeToUpdate.setCategory(category);
        financeToUpdate.setTransactionDate(finance.getTransactionDate());

        financeRepository.save(financeToUpdate);

        return ExpenseDTO.builder()
                .expense(financeToUpdate.getExpense())
                .categoryName(financeToUpdate.getCategory().getName())
                .transactionDate(financeToUpdate.getTransactionDate())
                .fileName(fileResponse)
                .build();
    }

    @Override
    public ExpenseDTO findById(Long id, String request) throws PfpException {
        User user = userRepository.findByEmailOrderById(jwtService.extractUserNameFromHeader(request));
        Optional<Finance> finance = financeRepository.findById(id);
        if (finance.isEmpty()) {
            throw new FinanceNotFoundException("Finance with ID " + id + " not found");
        } else if (!financeRepository.existsByIdAndUser(id, user)) {
            throw new UnauthorizedException("You are not authorized to see this finance");
        }
        Finance foundExpense = financeRepository.findByIdAndUser(id, user).orElse(null);
        if (Objects.isNull(foundExpense)) {
            throw new FinanceNotFoundException("You are not authorized to see this finance");
        }
        String filename = (foundExpense.getFileName() != null) ? foundExpense.getFileName() : "No file attached";
        return ExpenseDTO.builder()
                .expense(foundExpense.getExpense())
                .categoryName(foundExpense.getCategory().getName())
                .transactionDate(foundExpense.getTransactionDate())
                .fileName(filename)
                .build();
    }

    @Override
    public GetAllExpenseDTO findAllByUser(String request) throws PfpException {
        User user = userRepository.findByEmailOrderById(jwtService.extractUserNameFromHeader(request));
        if (financeRepository.findAllByUser(user).isEmpty()) {
            throw new NothingInsideException();
        }
        HashMap<Long, ExpenseDTO> expenses = new HashMap<>();
        for (int i = 0;
             i < financeRepository.findAllByUserAndExpenseNotNullOrderByTransactionDate(user).size();
             i++) {
            if (financeRepository.findAllByUserAndExpenseNotNullOrderByTransactionDate(user).get(i)
                    .getExpense() == 0) {
                continue;
            }
            expenses.put(
                    financeRepository.findAllByUserAndExpenseNotNullOrderByTransactionDate(user).get(i)
                            .getId(), ExpenseDTO.builder()
                            .expense(
                            financeRepository.findAllByUserAndExpenseNotNullOrderByTransactionDate(user).get(i).getExpense())
                            .categoryName(
                            financeRepository.findAllByUserAndExpenseNotNullOrderByTransactionDate(user).get(i).getCategory().getName())
                            .transactionDate(
                            financeRepository.findAllByUserAndExpenseNotNullOrderByTransactionDate(user).get(i).getTransactionDate())
                            .fileName(
                            financeRepository.findAllByUserAndExpenseNotNullOrderByTransactionDate(user).get(i).getFileName() != null ?
                                    financeRepository.findAllByUserAndExpenseNotNullOrderByTransactionDate(user).get(i).getFileName() : "No file attached")
                            .build());
        }
        return expenses.isEmpty() ? null : GetAllExpenseDTO.builder().expenses(expenses).build();
    }

    @Override
    public void deleteFinanceById(Long id, String request) throws PfpException {
        User user = userRepository.findByEmailOrderById(jwtService.extractUserNameFromHeader(request));
        Optional<Finance> financeToDelete = financeRepository.findById(id);
        if (financeToDelete.isEmpty()) {
            throw new FinanceNotFoundException("Finance with ID " + id + " not found");
        } else if (!financeRepository.existsByIdAndUser(id, user)) {
            throw new UnauthorizedException("You are not authorized to delete this finance");
        }
        financeRepository.delete(financeToDelete.get());
    }

    @Override
    public double getTotalExpenseBetweenDates(String request, LocalDate startDate, LocalDate endDate)
            throws DateRangeMissingException, DateRangeTooBigException {
        User user = userRepository.findByEmailOrderById(jwtService.extractUserNameFromHeader(request));
        List<Finance> expenseList =
                financeRepository.findByUserAndTransactionDateBetween(user, startDate, endDate);

        ExpenseServiceImpl.checkExceptions(startDate, endDate);
        if (startDate.isAfter(endDate)) {
            LocalDate temp = startDate;
            startDate = endDate;
            endDate = temp;
        }

        double totalExpense = expenseList.stream()
                .mapToDouble(finance -> finance.getExpense() != null ? finance.getExpense() : 0)
                .sum();
        return totalExpense;
    }

    @Override
    public List<MonthlyExpensesDTO> getTotalExpenseForPreviousAndCurrentMonth(String request, String monthType) throws PfpException {
        User user = userRepository.findByEmailOrderById(jwtService.extractUserNameFromHeader(request));

        LocalDate currentDate = LocalDate.now();
        LocalDate currentMonthStartDate = currentDate.withDayOfMonth(1);
        LocalDate currentMonthEndDate = currentDate.withDayOfMonth(currentDate.lengthOfMonth());

        LocalDate previousMonthStartDate;
        LocalDate previousMonthEndDate;

        if (monthType.equalsIgnoreCase("current")) {
            previousMonthStartDate = currentMonthStartDate;
            previousMonthEndDate = currentMonthEndDate;
        } else if (monthType.equalsIgnoreCase("previous")) {
            previousMonthStartDate = currentMonthStartDate.minusMonths(1);
            previousMonthEndDate = currentMonthStartDate.minusDays(1);
        } else {
            throw new PfpException("Invalid month offset value. Must be 'current' or 'previous'.");
        }

        List<Finance> expenseList = financeRepository.findByUserAndTransactionDateBetween(user, previousMonthStartDate, previousMonthEndDate);

        Map<String, Double> expensesMap = new HashMap<>();
        for (Finance expense : expenseList) {
            expensesMap.put(expense.getCategory().getName(), expensesMap.getOrDefault(expense.getCategory().getName(), 0.0) + expense.getExpense());
        }

        List<MonthlyExpensesDTO> expenseByMonth = expensesMap.entrySet().stream()
                .map(entry -> MonthlyExpensesDTO.builder()
                        .month(previousMonthStartDate.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH))
                        .category(entry.getKey())
                        .expenses(entry.getValue())
                        .build())
                .collect(Collectors.toList());

        return expenseByMonth;
    }


    @Override
    public List<MonthlyExpensesDTO> getExpenseByMonth(String request, int monthOffset) {
        User user = userRepository.findByEmailOrderById(jwtService.extractUserNameFromHeader(request));


        List<MonthlyExpensesDTO> expenseByMonth = new ArrayList<>();

        LocalDate startDate = LocalDate.now().withMonth(monthOffset).withDayOfMonth(1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);

        String monthDisplayName = startDate.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        List<Finance> expenseList = financeRepository.findByUserAndTransactionDateBetween(user, startDate, endDate);

        Map<String, Double> expensesMap = new HashMap<>();
        for (Finance expense : expenseList) {
            String expenseCategory = expense.getCategory().getName();
            double expenseAmount = expense.getExpense();

            expensesMap.put(expenseCategory, expensesMap.getOrDefault(expenseCategory, 0.0) + expenseAmount);
        }

        for (Map.Entry<String, Double> entry : expensesMap.entrySet()) {
            String expenseCategory = entry.getKey();
            double totalExpense = entry.getValue();

            expenseByMonth.add(MonthlyExpensesDTO.builder().month(monthDisplayName).category(expenseCategory).expenses(totalExpense).build());
        }

        return expenseByMonth;
    }


    private static void checkExceptions(LocalDate startDate, LocalDate endDate) throws DateRangeMissingException, DateRangeTooBigException {
        if (startDate == null) {
            throw new DateRangeMissingException();
        }
        if (endDate.isAfter(startDate.plusDays(365))) {
            throw new DateRangeTooBigException();
        }
    }

    @Override
    public GetExpenseDTO countAllExpenses(String request) throws PfpException {
        User user = userRepository.findByEmailOrderById(jwtService.extractUserNameFromHeader(request));
        double fullExpense = 0.0;
        if (financeRepository.findAllByUser(user).isEmpty()) {
            return GetExpenseDTO.builder().expense(fullExpense).categoryName("OTHER").build();
        }
        for (int i = 0;
             i < financeRepository.findAllByUserAndExpenseNotNullOrderByTransactionDate(user).size();
             i++) {
            fullExpense = fullExpense +
                    financeRepository.findAllByUserAndExpenseNotNullOrderByTransactionDate(user).get(i)
                            .getExpense();
        }
        return GetExpenseDTO.builder().expense(fullExpense).categoryName("OTHER").build();
    }

    @Override
    public List<FinancesMonthlyDTO> monthlyExpense(String request) throws PfpException {
        User user = userRepository.findByEmailOrderById(jwtService.extractUserNameFromHeader(request));
        List<FinancesMonthlyDTO> expensesMonthly = new ArrayList<>();
        List<String> months = List.of("January", "February", "March", "April", "May", "Jun", "July", "August", "September", "October", "November", "December");

        if (financeRepository.findAllByUser(user).isEmpty()) {
            throw new NothingInsideException();
        }

        for (int j = 1; j <= 12; j++) {
            String month = months.get(j - 1);
            List<Finance> expenses = financeRepository
                    .findByUserAndTransactionDateBetween(user,
                            LocalDate.of(LocalDate.now().getYear(), j, 1),
                            LocalDate.of(LocalDate.now().getYear(), j,
                                    LocalDate.of(LocalDate.now().getYear(), j, 1)
                                            .lengthOfMonth()));

            expensesMonthly.add(FinancesMonthlyDTO.builder().month(month).finances(expenses.stream().mapToDouble(Finance::getExpense).sum()).build());
        }

        return expensesMonthly;
    }

}