package com.mth.demo.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mth.demo.exception.NothingInsideException;
import com.mth.demo.exception.PfpException;
import com.mth.demo.exception.UnauthorizedException;
import com.mth.demo.exception.dataexportexceptions.DateRangeMissingException;
import com.mth.demo.exception.dataexportexceptions.DateRangeTooBigException;
import com.mth.demo.exception.financeexceptions.FinanceNotFoundException;
import com.mth.demo.models.dto.finance.FinancesMonthlyDTO;
import com.mth.demo.models.dto.finance.GetAllIncomeDTO;
import com.mth.demo.models.dto.finance.GetIncomeDTO;
import com.mth.demo.models.dto.finance.IncomeDTO;
import com.mth.demo.models.entities.finance.Category;
import com.mth.demo.models.entities.finance.Finance;
import com.mth.demo.models.entities.finance.IncomeType;
import com.mth.demo.models.entities.user.User;
import com.mth.demo.repositories.CategoryRepository;
import com.mth.demo.repositories.FinanceRepository;
import com.mth.demo.repositories.UserRepository;
import com.mth.demo.security.config.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class IncomeServiceImpl implements IncomeService {

    private final JwtService jwtService;
    private final FinanceRepository financeRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ObjectMapper objectMapper;

    private final LocalDate recurringDateTo = LocalDate.now().plusMonths(1L);

    @Override
    public GetIncomeDTO createNewIncome(String income, MultipartFile file, String request)
            throws FinanceNotFoundException, IOException {
        User user = userRepository.findByEmailOrderById(jwtService.extractUserNameFromHeader(request));

        Finance finance = objectMapper.readValue(income, Finance.class);
        if (Objects.isNull(finance.getIncome())) {
            finance.setIncome(0.);
        }
        if (finance.getIncomeType() == null) {
            finance.setIncomeType(IncomeType.OTHER);
        }
        if (finance.getTransactionDate() == null) {
            finance.setTransactionDate(LocalDate.now());
        }
        if (finance.getRecurring() == null) {
            finance.setRecurring(false);
        }
        Category category = getCategory();
        Finance newIncome = new Finance(finance.getIncome(), category, finance.getRecurring(),
                finance.getTransactionDate(), finance.getIncomeType(), user);
        String fileResponse = "No file uploaded";

        if (file != null && !file.isEmpty()) {
            if (file.getSize() > 4 * 1024 * 1024) {
                throw new IOException("File is too big");
            }
            finance.setFileName(file.getOriginalFilename());
            finance.setFileType(file.getContentType());
            finance.setFileData(file.getBytes());
            fileResponse = file.getOriginalFilename();
        }
        financeRepository.save(newIncome);
        checkAndSetRecurring(user);
        return GetIncomeDTO.builder().id((long) financeRepository.findAll().toArray().length)
                .income(finance.getIncome())
                .recurring(finance.getRecurring())
                .incomeType(finance.getIncomeType().name())
                .transactionDate(finance.getTransactionDate())
                .fileName(fileResponse)
                .build();
    }

    private Category getCategory() throws FinanceNotFoundException {
        Optional<Category> categoryOptional = categoryRepository.findByName("other");
        if (categoryOptional.isEmpty()) {
            throw new FinanceNotFoundException("Category 'other' not found!");
        }
        return categoryOptional.get();
    }

    @Override
    public GetAllIncomeDTO getAllIncomes(String request) throws FinanceNotFoundException {
        User user = userRepository.findByEmailOrderById(jwtService.extractUserNameFromHeader(request));
        checkAndSetRecurring(user);
        List<Finance> incomeList =
                financeRepository.findAllByUserAndIncomeNotNullOrderByTransactionDate(user);
        if (incomeList.isEmpty()) {
            throw new FinanceNotFoundException(
                    "No income has been found for user with id: " + user.getId());
        }
        HashMap<Long, IncomeDTO> incomeDtoHashMap = new HashMap<>();
        for (Finance finance : incomeList) {
            if (finance.getIncome() == 0.) {
                continue;
            }
            incomeDtoHashMap.put(finance.getId(),
                    IncomeDTO.builder().income(finance.getIncome()).recurring(finance.getRecurring())
                            .transactionDate(finance.getTransactionDate()).incomeType(finance.getIncomeType())
                            .build());
        }

        return incomeDtoHashMap.isEmpty() ? null :
                GetAllIncomeDTO.builder().incomes(incomeDtoHashMap).build();
    }

    @Override
    public GetIncomeDTO updateIncome(Long id, String income, MultipartFile file, String request)
            throws UnauthorizedException, FinanceNotFoundException, IOException {
        User user = userRepository.findByEmailOrderById(jwtService.extractUserNameFromHeader(request));

        Finance financeMap = objectMapper.readValue(income, Finance.class);
        Finance finance = checkAndFindFinance(id, user);
        finance.setIncome(financeMap.getIncome());
        finance.setRecurring(financeMap.getRecurring());
        finance.setIncomeType(financeMap.getIncomeType());
        finance.setTransactionDate(financeMap.getTransactionDate());
        String fileResponse = "No file uploaded";

        if (file != null && !file.isEmpty()) {
            if (file.getSize() > 4 * 1024 * 1024) {
                throw new IOException("File is too big");
            }
            finance.setFileName(file.getOriginalFilename());
            finance.setFileType(file.getContentType());
            finance.setFileData(file.getBytes());
            fileResponse = file.getOriginalFilename();
        } else if (finance.getFileName() != null && !finance.getFileName().isEmpty()) {
            fileResponse = finance.getFileName();
        }
        financeRepository.save(finance);
        checkAndSetRecurring(user);
        return GetIncomeDTO.builder()
                .id(finance.getId())
                .income(finance.getIncome())
                .recurring(finance.getRecurring())
                .incomeType(finance.getIncomeType().name())
                .transactionDate(finance.getTransactionDate())
                .fileName(fileResponse)
                .build();
    }

    @Override
    public void deleteIncome(Long id, String request)
            throws UnauthorizedException, FinanceNotFoundException {
        User user = userRepository.findByEmailOrderById(jwtService.extractUserNameFromHeader(request));
        Finance finance = checkAndFindFinance(id, user);
        financeRepository.delete(finance);
    }

    private Finance checkAndFindFinance(Long id, User user)
            throws FinanceNotFoundException, UnauthorizedException {
        Optional<Finance> financeOptional = financeRepository.findById(id);
        if (financeOptional.isEmpty()) {
            throw new FinanceNotFoundException("Income with id '" + id + "' not found.");
        }
        financeOptional = financeRepository.findByIdAndUser(id, user);
        if (financeOptional.isEmpty()) {
            throw new UnauthorizedException("You are not authorized to income Nr. '" + id + "'");
        }
        return financeOptional.get();
    }

    private void checkAndSetRecurring(User user) throws FinanceNotFoundException {
        Optional<Finance> financeOptional;
        for (IncomeType type : IncomeType.values()) {
            financeOptional = getLatestOccurrence(user, type);
            if (financeOptional.isEmpty()) {
                continue;
            }
            addMissingRecurringIncomes(user, financeOptional.get(), type);
        }
    }

    private void addMissingRecurringIncomes(User user, Finance finance, IncomeType type)
            throws FinanceNotFoundException {
        LocalDate lastOccurrenceDate = finance.getTransactionDate();
        Optional<Finance> financeOptionalNewDate;
        Category category = getCategory();
        for (LocalDate date = lastOccurrenceDate; date.isBefore(recurringDateTo);
             date = date.plusMonths(1)) {
            financeOptionalNewDate =
                    financeRepository.findByUserAndIncomeTypeAndTransactionDate(user, type, date);
            if (financeOptionalNewDate.isEmpty()) {
                Finance newfinance =
                        new Finance(finance.getIncome(), category, finance.getRecurring(), date,
                                finance.getIncomeType(), finance.getUser());
                financeRepository.save(newfinance);
            }
        }
    }

    private Optional<Finance> getLatestOccurrence(User user, IncomeType type) {
        Optional<Finance> financeOptional;
        financeOptional =
                financeRepository.findFirstByUserAndRecurringAndIncomeTypeAndIncomeNotNullOrderByTransactionDateDesc(
                        user, true, type);
        return financeOptional;
    }

    @Override
    public double getTotalIncomeBetweenDates(String request, LocalDate startDate, LocalDate endDate)
            throws DateRangeMissingException, DateRangeTooBigException {
        User user = userRepository.findByEmailOrderById(jwtService.extractUserNameFromHeader(request));
        List<Finance> incomeList =
                financeRepository.findByUserAndTransactionDateBetween(user, startDate, endDate);

        IncomeServiceImpl.checkExceptions(startDate, endDate);
        if (startDate.isAfter(endDate)) {
            LocalDate temp = startDate;
            startDate = endDate;
            endDate = temp;
        }

        double totalIncome = incomeList.stream()
                .mapToDouble(finance -> finance.getIncome() != null ? finance.getIncome() : 0)
                .sum();
        return totalIncome;
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
    public List<FinancesMonthlyDTO> monthlyIncomes(String request) throws PfpException {
        User user = userRepository.findByEmailOrderById(jwtService.extractUserNameFromHeader(request));

        List<FinancesMonthlyDTO> incomesMonthly = new ArrayList<>();
        List<String> months =
                List.of("January", "February", "March", "April", "May", "Jun", "July", "August",
                        "September", "October", "November", "December");

        if (financeRepository.findAllByUser(user).isEmpty()) {
            throw new NothingInsideException();
        }

        for (int j = 1; j <= 12; j++) {
            String month = months.get(j - 1);
            List<Finance> incomes = financeRepository.findByUserAndTransactionDateBetween(user,
                    LocalDate.of(LocalDate.now().getYear(), j, 1), LocalDate.of(LocalDate.now().getYear(), j,
                            LocalDate.of(LocalDate.now().getYear(), j, 1).lengthOfMonth()));

            incomesMonthly.add(FinancesMonthlyDTO.builder().month(month)
                    .finances(incomes.stream().mapToDouble(Finance::getIncome).sum()).build());
        }

        return incomesMonthly;
    }

}