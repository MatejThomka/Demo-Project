package com.mth.demo.services;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mth.demo.exception.NothingInsideException;
import com.mth.demo.exception.PfpException;
import com.mth.demo.exception.UnauthorizedException;
import com.mth.demo.exception.financeexceptions.FinanceNotFoundException;
import com.mth.demo.models.dto.finance.ExpenseDTO;
import com.mth.demo.models.entities.finance.Category;
import com.mth.demo.models.entities.finance.Finance;
import com.mth.demo.models.entities.user.RoleType;
import com.mth.demo.models.entities.user.User;
import com.mth.demo.repositories.CategoryRepository;
import com.mth.demo.repositories.FinanceRepository;
import com.mth.demo.repositories.UserRepository;
import com.mth.demo.security.config.JwtService;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class ExpenseServiceImplTest {

    @Mock
    private FinanceRepository financeRepositoryMock;

    @Mock
    private JwtService jwtServiceMock;

    @Mock
    private UserRepository userRepositoryMock;

    @Mock
    private CategoryRepository categoryRepositoryMock;

    @Mock
    private ObjectMapper objectMapperMock;

    @InjectMocks
    private ExpenseServiceImpl expenseService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        expenseService = new ExpenseServiceImpl(financeRepositoryMock, jwtServiceMock, userRepositoryMock, categoryRepositoryMock, objectMapperMock);
    }

    @Test
    void saveExpense_expenseTypeGiven_checkCorrectExpenseType() throws IOException, FinanceNotFoundException {
        // Arrange
        User user = new User();
        String request = "example-request";

        String newExpense = "{\"expense\": 1000.00, \"category\": \"clothes\", \"transactionDate\": \"2023-06-11\"}";
        Finance finance = new Finance();
        finance.setExpense(1000.00);
        finance.setCategory(new Category(1L, "clothes"));
        finance.setTransactionDate(LocalDate.of(2023, 6, 11));

        String expectedCategoryName = "clothes";

        // Mock the repository method calls
        when(userRepositoryMock.findByEmailOrderById(anyString())).thenReturn(user);
        when(objectMapperMock.readValue(anyString(), eq(Finance.class))).thenReturn(finance);
        when(categoryRepositoryMock.findByName(anyString())).thenReturn(Optional.of(finance.getCategory()));

        // Act
        ExpenseDTO result = expenseService.saveExpense(newExpense, null, request);

        // Assert
        assertEquals(expectedCategoryName, result.getCategoryName());
    }

    @Test
    void saveExpense_expenseTypeNotGiven_setExpenseTypeToOther() throws FinanceNotFoundException, IOException {
        // Arrange
        User user = new User();
        String request = "example-request";

        String newExpense = "{\"expense\": 1000.00, \"category\": \"\", \"transactionDate\": \"2023-06-11\"}";
        Finance finance = new Finance();
        finance.setExpense(1000.00);
        finance.setCategory(new Category(1L, "other"));
        finance.setTransactionDate(LocalDate.of(2023, 6, 11));

        String expectedCategoryName = "other";

        // Mock the repository method calls
        when(userRepositoryMock.findByEmailOrderById(anyString())).thenReturn(user);
        when(objectMapperMock.readValue(anyString(), eq(Finance.class))).thenReturn(finance);
        when(categoryRepositoryMock.findByName(anyString())).thenReturn(Optional.of(finance.getCategory()));

        // Act
        ExpenseDTO result = expenseService.saveExpense(newExpense, null, request);

        // Assert
        assertEquals(expectedCategoryName, result.getCategoryName());
    }

    @Test
    void updateExpense_noFileProvided_keepOldFile() throws PfpException, IOException {
        // Arrange
        User user = new User(1L, "firstName", "lastName", "mail@mail", "password", LocalDate.now(), RoleType.USER);
        Finance expense = new Finance(
                100.0,
                new Category(1L, "clothes"),
                LocalDate.of(2016, 3, 8),
                user
        );
        expense.setId(1L);
        expense.setFileName("old-file");
        Category category = new Category(1L, "food");
        String updateExpense = "{\"expense\": 1000.00, \"category\": \"food\"}";
        ExpenseDTO expenseDTO = new ExpenseDTO(
                1000.00,
                category.getName(),
                LocalDate.now(),
                "old-file"
        );
        Finance updatedExpense = new Finance(
                1000.00,
                category,
                LocalDate.now(),
                user
        );

        // Mock the repository method calls
        when(jwtServiceMock.extractUserNameFromHeader(anyString())).thenReturn(user.getEmail());
        when(userRepositoryMock.findByEmailOrderById(user.getEmail())).thenReturn(user);
        when(objectMapperMock.readValue(updateExpense, Finance.class)).thenReturn(updatedExpense);
        when(financeRepositoryMock.findById(expense.getId())).thenReturn(Optional.of(expense));
        when(categoryRepositoryMock.findByName(any())).thenReturn(Optional.of(category));
        when(financeRepositoryMock.existsByIdAndUser(anyLong(), any())).thenReturn(true);

        // Act
        var result = expenseService.updateExpense(updateExpense, 1L, null, "request");

        // Assert
        assertEquals(expenseDTO, result);
    }

    @Test
    void updateExpense_wrongTokenGiven_returnErrorMessage() throws JsonProcessingException {
        // Arrange
        String finance = "{\"expense\": 1000.00, \"category\": \"other\"}";
        var user = new User(1L, "test", "test", "test@test.com", "test", LocalDate.now(), RoleType.USER);
        var category = new Category(1L, 1L, "other");
        var newExpense = new Finance(1L, 1000.00, category, LocalDate.now(), user);
        var wrongToken = "wrongToken";

        // Mock the repository method calls
        when(financeRepositoryMock.findById(newExpense.getId())).thenReturn(Optional.of(newExpense));
        when(objectMapperMock.readValue(anyString(), eq(Finance.class))).thenReturn(newExpense);
        when(categoryRepositoryMock.findByName(anyString())).thenReturn(Optional.of(category));

        // Act and assert
        Exception exception = assertThrows(UnauthorizedException.class, () ->
                expenseService.updateExpense(finance, newExpense.getId(), null, wrongToken)
        );
        String expectedMessage = "You are not authorized to update this finance";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void updateExpense_expenseTypeGiven_checkCorrectExpenseType() throws JsonProcessingException {
        // Arrange
        var category = new Category(1L, 1L, "other");

        String expense = "other";
        Finance finance = new Finance();
        finance.setExpense(1000.00);
        finance.setCategory(category);

        // Mock the repository method calls
        when(objectMapperMock.readValue(anyString(), eq(Finance.class))).thenReturn(finance);
        when(categoryRepositoryMock.findByName(expense)).thenReturn(Optional.of(category));

        // Act and assert
        Assertions.assertTrue(categoryRepositoryMock.findByName(finance.getCategory().getName()).isPresent());
    }

    @Test
    void updateExpense_expenseTypeNotGiven_setExpenseTypeToOldOne() throws PfpException, IOException {
        // Arrange
        User user = new User(1L, "test", "test", "test@test.com", "test", LocalDate.now(), RoleType.USER);

        String updateExpense = "{\"expense\": \"1000.00\", \"category\": \"\", \"transactionDate\": \"2023-06-11\"}";
        Category category = new Category(1L, 1L, "food");
        Finance financeToUpdate = new Finance(1L, 2000.00, category, LocalDate.now(), user);
        Finance financeCorrect = new Finance(1L, 1000.00, category, LocalDate.now(), user);
        ExpenseDTO response = new ExpenseDTO(1000.00, "food", LocalDate.now(), "No file uploaded");

        // Mock the repository method calls
        when(userRepositoryMock.save(user)).thenReturn(user);
        when(jwtServiceMock.extractUserNameFromHeader(any())).thenReturn(user.getEmail());
        when(userRepositoryMock.findByEmailOrderById(user.getEmail())).thenReturn(user);
        when(objectMapperMock.readValue(anyString(), eq(Finance.class))).thenReturn(financeCorrect);
        when(categoryRepositoryMock.findByName(any())).thenReturn(Optional.of(category));
        when(financeRepositoryMock.findById(1L)).thenReturn(Optional.of(financeToUpdate));
        when(financeRepositoryMock.findByIdAndUser(1L, user)).thenReturn(Optional.of(financeToUpdate));
        when(financeRepositoryMock.existsByIdAndUser(1L, user)).thenReturn(true);

        // Act
        var result = expenseService.updateExpense(updateExpense, 1L, null, null);

        // Assert
        assertEquals(response, result);
    }

    @Test
    void findById_incorrectExpenseId_returnErrorMessage() {
        // Arrange
        var user = new User(1L, "test", "test", "test@test.com", "test", LocalDate.now(), RoleType.USER);
        var category = new Category(1L, 1L, "other");
        var newExpense = new Finance(1L, 1000.00, category, LocalDate.now(), user);

        // Act and assert
        Exception exception = assertThrows(FinanceNotFoundException.class, () ->
                expenseService.findById(newExpense.getId(), null)
        );
        String expectedMessage = "Finance with ID 1 not found";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void findById_wrongTokenGiven_returnErrorMessage() {
        // Arrange
        var user = new User(1L, "test", "test", "test@test.com", "test", LocalDate.now(), RoleType.USER);
        var category = new Category(1L, 1L, "other");
        var newExpense = new Finance(1L, 1000.00, category, LocalDate.now(), user);
        var wrongToken = "wrongToken";

        // Mock the repository method calls
        when(financeRepositoryMock.findById(newExpense.getId())).thenReturn(Optional.of(newExpense));

        // Act and assert
        Exception exception = assertThrows(UnauthorizedException.class, () ->
                expenseService.findById(newExpense.getId(), wrongToken)
        );
        String expectedMessage = "You are not authorized to see this finance";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void findAllByUser_noExpenseOnUser_returnErrorMessage() {
        // Arrange
        var user = new User(1L, "test", "test", "test@test.com", "test", LocalDate.now(), RoleType.USER);

        // Mock the repository method calls
        when(jwtServiceMock.extractUserNameFromHeader("request")).thenReturn(user.getEmail());
        when(userRepositoryMock.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepositoryMock.findByEmailOrderById(user.getEmail())).thenReturn(user);
        when(financeRepositoryMock.findAllByUser(user)).thenReturn(new ArrayList<>());

        // Act, Assert
        Assertions.assertThrows(NothingInsideException.class,
                () -> expenseService.findAllByUser(null));
    }

    @Test
    void deleteFinanceById_incorrectExpenseId_returnErrorMessage() {
        // Arrange
        var user = new User(1L, "test", "test", "test@test.com", "test", LocalDate.now(), RoleType.USER);
        var category = new Category(1L, 1L, "other");
        var newExpense = new Finance(1L, 1000.00, category, LocalDate.now(), user);

        // Act and assert
        Exception exception = assertThrows(FinanceNotFoundException.class, () ->
                expenseService.deleteFinanceById(newExpense.getId(), null)
        );
        String expectedMessage = "Finance with ID 1 not found";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void deleteFinanceById_wrongTokenGiven_returnErrorMessage() {
        // Arrange
        var user = new User(1L, "test", "test", "test@test.com", "test", LocalDate.now(), RoleType.USER);
        var category = new Category(1L, 1L, "other");
        var newExpense = new Finance(1L, 1000.00, category, LocalDate.now(), user);
        var wrongToken = "wrongToken";

        // Mock the repository method calls
        when(financeRepositoryMock.findById(newExpense.getId())).thenReturn(Optional.of(newExpense));

        // Act and assert
        Exception exception = assertThrows(UnauthorizedException.class, () ->
                expenseService.deleteFinanceById(newExpense.getId(), wrongToken)
        );
        String expectedMessage = "You are not authorized to delete this finance";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

}