package com.mth.demo.services;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mth.demo.exception.UnauthorizedException;
import com.mth.demo.exception.financeexceptions.FinanceNotFoundException;
import com.mth.demo.models.dto.finance.GetIncomeDTO;
import com.mth.demo.models.entities.finance.Category;
import com.mth.demo.models.entities.finance.Finance;
import com.mth.demo.models.entities.finance.IncomeType;
import com.mth.demo.models.entities.user.RoleType;
import com.mth.demo.models.entities.user.User;
import com.mth.demo.repositories.CategoryRepository;
import com.mth.demo.repositories.FinanceRepository;
import com.mth.demo.repositories.UserRepository;
import com.mth.demo.security.config.JwtService;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class IncomeServiceImplTest {

    @Mock
    private UserRepository userRepositoryMock = mock(UserRepository.class);

    @Mock
    private JwtService jwtServiceMock = mock(JwtService.class);

    @Mock
    private FinanceRepository financeRepositoryMock = mock(FinanceRepository.class);

    @Mock
    private CategoryRepository categoryRepositoryMock = mock(CategoryRepository.class);

    @Mock
    private ObjectMapper objectMapperMock = mock(ObjectMapper.class);

    @InjectMocks
    private IncomeServiceImpl incomeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        incomeService = new IncomeServiceImpl(jwtServiceMock, financeRepositoryMock, userRepositoryMock, categoryRepositoryMock, objectMapperMock);
    }

    @Test
    void createNewIncome_returnJsonOK() throws FinanceNotFoundException, IOException {
        //arrange
        var user = new User(1L, "firstName", "lastName", "mail@mail", "password", LocalDate.now(), RoleType.USER);
        String income = "{\"income\": 100.0, \"recurring\": false, \"transactionDate\": \"2016-03-08\", \"incomeType\": \"WORK\"}";
        GetIncomeDTO getIncomeDto = new GetIncomeDTO(0L, 100.0, false, LocalDate.of(2016, 3, 8), IncomeType.WORK.toString(), "No file uploaded");
        var category = new Category(1L, "other");
        Finance finance = new Finance(
                100.0,
                category,
                false,
                LocalDate.of(2016, 3, 8),
                IncomeType.WORK,
                user);

        // Mock the repository method calls
        when(jwtServiceMock.extractUserNameFromHeader(any())).thenReturn(user.getEmail());
        when(userRepositoryMock.findByEmailOrderById(user.getEmail())).thenReturn(user);
        when(objectMapperMock.readValue(income, Finance.class)).thenReturn(finance);
        when(categoryRepositoryMock.findByName("other")).thenReturn(Optional.of(category));
        when(financeRepositoryMock.save(any())).thenReturn(finance);
        when(categoryRepositoryMock.findByName("other")).thenReturn(Optional.of(category));

        //act
        var result = incomeService.createNewIncome(income, null, any());

        //assert
        Assertions.assertEquals(getIncomeDto, result);
    }

    @Test
    void readAllRecords_noIncomeOnUser_returnErrorMessage() {
        //arrange
        var user = new User(1L, "firstName", "lastName", "mail@mail",
                "password", LocalDate.now(), RoleType.USER);

        // Mock the repository method calls
        when(jwtServiceMock.extractUserNameFromHeader(any())).thenReturn(user.getEmail());
        when(userRepositoryMock.findByEmailOrderById(user.getEmail())).thenReturn(user);

        //act and assert
        Exception exception = assertThrows(FinanceNotFoundException.class, () ->
                incomeService.getAllIncomes("request")
        );
        String expectedMessage = "No income has been found for user with id: 1";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void updateIncome_returnJsonOK() throws UnauthorizedException, FinanceNotFoundException, IOException {
        // Arrange
        var user = new User(1L, "firstName", "lastName", "mail@mail", "password", LocalDate.now(), RoleType.USER);
        String income = "{\"income\": 100.0, \"recurring\": false, \"transactionDate\": \"2016-03-08\", \"incomeType\": \"WORK\"}";
        GetIncomeDTO getIncomeDto = new GetIncomeDTO(1L, 100.0, false, LocalDate.of(2016, 3, 8), IncomeType.WORK.toString(), "No file uploaded");
        Finance finance = new Finance(
                1L,
                100.0,
                null,
                false,
                LocalDate.of(2016, 3, 8),
                IncomeType.WORK,
                user);

        // Mock the repository method calls
        when(jwtServiceMock.extractUserNameFromHeader(any())).thenReturn(user.getEmail());
        when(userRepositoryMock.findByEmailOrderById(user.getEmail())).thenReturn(user);
        when(objectMapperMock.readValue(income, Finance.class)).thenReturn(finance);
        when(financeRepositoryMock.findById(any())).thenReturn(Optional.of(finance));
        when(financeRepositoryMock.findByIdAndUser(any(), any())).thenReturn(Optional.of(finance));

        // Act
        var result = incomeService.updateIncome(1L, income, null, "request");

        // Assert
        Assertions.assertEquals(getIncomeDto, result);
    }

    @Test
    void updateIncome_notExistFinance_OK() {
        //arrange
        String income = "{\"income\": 100.0, \"recurring\": false, \"transactionDate\": \"2016-03-08\", \"incomeType\": \"WORK\"}";
        long idRec = 7L;

        //act and assert
        Exception exception = Assertions.assertThrows(
                FinanceNotFoundException.class,
                () -> incomeService.updateIncome(7L, income, null, "request"));

        Assertions.assertEquals("Income with id '" + idRec + "' not found."
                , exception.getMessage());
    }

    @Test
    void updateIncome_notAuthorisedUser_OK() {
        //arrange
        var user1 = new User(1L, "firstName", "lastName"
                , "mail@mail", "password", LocalDate.now(), RoleType.USER);
        var user2 = new User(2L, "firstName2", "lastName2"
                , "mail@mail2", "password2", LocalDate.now(), RoleType.USER);
        String income = "{\"income\": 100.0, \"recurring\": false, \"transactionDate\": \"2016-03-08\", \"incomeType\": \"WORK\"}";
        Finance finance = new Finance(
                1L,
                100.0,
                null,
                false,
                LocalDate.of(2016, 3, 8),
                IncomeType.WORK,
                user1);
        long recId = 7L;

        // Mock the repository method calls
        when(jwtServiceMock.extractUserNameFromHeader("request")).thenReturn(user2.getEmail());
        when(userRepositoryMock.findByEmailOrderById(user1.getEmail())).thenReturn(user1);
        when(financeRepositoryMock.findById(recId)).thenReturn(Optional.of(finance));

        //act and assert
        Exception exception = Assertions.assertThrows(
                UnauthorizedException.class,
                () -> incomeService.updateIncome(7L, income, null, "request"));

        Assertions.assertEquals("You are not authorized to income Nr. '" + recId + "'", exception.getMessage());
    }

}