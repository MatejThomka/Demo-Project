package com.mth.demo.handler.limit;

import com.gfa.pfp.handler.limit.LimitHandler;
import com.gfa.pfp.models.dto.limit.LimitDTO;
import com.gfa.pfp.models.dto.limit.LimitResponseDTO;
import com.gfa.pfp.models.entities.finance.Category;
import com.gfa.pfp.models.entities.finance.Finance;
import com.gfa.pfp.models.entities.finance.Limit;
import com.gfa.pfp.models.entities.user.User;
import com.gfa.pfp.repositories.CategoryRepository;
import com.gfa.pfp.repositories.FinanceRepository;
import com.gfa.pfp.repositories.LimitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class LimitHandlerTest {

    @Mock
    private LimitRepository limitRepository;

    @Mock
    private FinanceRepository financeRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private LimitHandler limitHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        limitHandler = new LimitHandler(limitRepository, financeRepository, categoryRepository);
    }

    @Test
    public void testGetLimitByCategoryName_CategoryExistsForUser_ReturnsOptionalLimit() {
        // Arrange
        LimitDTO limitDTO = new LimitDTO();
        limitDTO.setCategory("some_category");

        User user = new User();

        Category category = new Category();
        category.setName("some_category");

        Limit limit = new Limit();
        limit.setCategory(category);
        limit.setUser(user);

        // Mock the repository methods
        when(categoryRepository.findByUserIdAndName(user.getId(), limitDTO.getCategory().toLowerCase())).thenReturn(Optional.of(category));
        when(limitRepository.findByCategoryAndUser(category, user)).thenReturn(Optional.of(limit));

        // Act
        Optional<Limit> result = limitHandler.getLimitByCategoryName(limitDTO, user);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(limit.getId(), result.get().getId());
    }

    @Test
    public void testGetLimitByCategoryName_CategoryDoesNotExistForUser_ReturnsOptionalLimitFromDefaultCategory() {
        // Arrange
        LimitDTO limitDTO = new LimitDTO();
        limitDTO.setCategory("some_category");

        User user = new User();

        Category defaultCategory = new Category();
        defaultCategory.setName("default_category");

        Limit limit = new Limit();
        limit.setCategory(defaultCategory);
        limit.setUser(user);

        // Mock the repository methods
        when(categoryRepository.findByUserIdAndName(user.getId(), limitDTO.getCategory().toLowerCase())).thenReturn(Optional.empty());
        when(categoryRepository.findByUserIdAndName(-1L, limitDTO.getCategory().toLowerCase())).thenReturn(Optional.of(defaultCategory));
        when(limitRepository.findByCategoryAndUser(defaultCategory, user)).thenReturn(Optional.of(limit));

        // Act
        Optional<Limit> result = limitHandler.getLimitByCategoryName(limitDTO, user);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(limit.getId(), result.get().getId());
    }

    @Test
    public void testGetCategoryByName_CategoryExistsForUser_ReturnsOptionalCategory() {
        // Arrange
        LimitDTO limitDTO = new LimitDTO();
        limitDTO.setCategory("some_category");

        Long userId = 1L;

        Category category = new Category();
        category.setName("some_category");

        // Mock the repository method
        when(categoryRepository.findByUserIdAndName(userId, limitDTO.getCategory().toLowerCase())).thenReturn(Optional.of(category));

        // Act
        Optional<Category> result = limitHandler.getCategoryByName(limitDTO, userId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(category.getId(), result.get().getId());
    }

    @Test
    public void testGetCategoryByName_CategoryDoesNotExistForUser_ReturnsOptionalDefaultCategory() {
        // Arrange
        LimitDTO limitDTO = new LimitDTO();
        limitDTO.setCategory("some_category");

        Long userId = 1L;

        Category defaultCategory = new Category();
        defaultCategory.setName("default_category");

        // Mock the repository methods
        when(categoryRepository.findByUserIdAndName(userId, limitDTO.getCategory().toLowerCase())).thenReturn(Optional.empty());
        when(categoryRepository.findByUserIdAndName(-1L, limitDTO.getCategory().toLowerCase())).thenReturn(Optional.of(defaultCategory));

        // Act
        Optional<Category> result = limitHandler.getCategoryByName(limitDTO, userId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(defaultCategory.getId(), result.get().getId());
    }

    @Test
    public void testGetCategoryByName_CategoryDoesNotExistForUserAndDefaultCategory_ReturnsEmptyOptional() {
        // Arrange
        LimitDTO limitDTO = new LimitDTO();
        limitDTO.setCategory("some_category");

        Long userId = 1L;

        // Mock the repository methods
        when(categoryRepository.findByUserIdAndName(userId, limitDTO.getCategory().toLowerCase())).thenReturn(Optional.empty());
        when(categoryRepository.findByUserIdAndName(-1L, limitDTO.getCategory().toLowerCase())).thenReturn(Optional.empty());

        // Act
        Optional<Category> result = limitHandler.getCategoryByName(limitDTO, userId);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    public void testGetLimitResponseDTO_LimitNotExceeded_ReturnsLimitResponseDTO() {
        // Arrange
        LimitDTO limitDTO = new LimitDTO();
        limitDTO.setCategory("some_category");
        limitDTO.setStart(LocalDate.parse("2023-06-01"));
        limitDTO.setEnd(LocalDate.parse("2023-06-30"));
        limitDTO.setExpenseLimit(1000.0);

        User user = new User();

        Category category = new Category();
        category.setName("some_category");

        Finance finance1 = new Finance();
        finance1.setUser(user);
        finance1.setCategory(category);
        finance1.setTransactionDate(LocalDate.parse("2023-06-10"));
        finance1.setExpense(500.0);

        Finance finance2 = new Finance();
        finance2.setUser(user);
        finance2.setCategory(category);
        finance2.setTransactionDate(LocalDate.parse("2023-06-20"));
        finance2.setExpense(300.0);

        List<Finance> expenseList = Arrays.asList(finance1, finance2);

        // Mock the repository methods
        when(categoryRepository.findByUserIdAndName(user.getId(), limitDTO.getCategory().toLowerCase())).thenReturn(Optional.of(category));
        when(financeRepository.findByUserIdAndCategoryIdAndTransactionDateBetween(
                user.getId(),
                category.getId(),
                limitDTO.getStart(),
                limitDTO.getEnd()
        )).thenReturn(expenseList);

        // Act
        LimitResponseDTO result = limitHandler.getLimitResponseDTO(limitDTO, user);

        // Assert
        assertEquals(category.getName(), result.getCategory());
        assertEquals(limitDTO.getExpenseLimit(), result.getLimit());
        assertEquals(800.0, result.getCurrent(), 0.001);
        assertEquals("200.0", result.getRemaining());
        assertEquals("80.0 %", result.getPercentage());
        assertEquals("20.0 %", result.getPercentageRemaining());
        assertEquals(limitDTO.getStart(), result.getStart());
        assertEquals(limitDTO.getEnd(), result.getEnd());
    }

    @Test
    public void testGetLimitResponseDTO_LimitExceeded_ReturnsLimitResponseDTOWithNegativeValues() {
        // Arrange
        LimitDTO limitDTO = new LimitDTO();
        limitDTO.setCategory("some_category");
        limitDTO.setStart(LocalDate.parse("2023-06-01"));
        limitDTO.setEnd(LocalDate.parse("2023-06-30"));
        limitDTO.setExpenseLimit(1000.0);

        User user = new User();

        Category category = new Category();
        category.setName("some_category");

        Finance finance1 = new Finance();
        finance1.setUser(user);
        finance1.setCategory(category);
        finance1.setTransactionDate(LocalDate.parse("2023-06-10"));
        finance1.setExpense(1200.0);

        Finance finance2 = new Finance();
        finance2.setUser(user);
        finance2.setCategory(category);
        finance2.setTransactionDate(LocalDate.parse("2023-06-20"));
        finance2.setExpense(900.0);

        List<Finance> expenseList = Arrays.asList(finance1, finance2);

        // Mock the repository methods
        when(categoryRepository.findByUserIdAndName(user.getId(), limitDTO.getCategory().toLowerCase())).thenReturn(Optional.of(category));
        when(financeRepository.findByUserIdAndCategoryIdAndTransactionDateBetween(
                user.getId(),
                category.getId(),
                limitDTO.getStart(),
                limitDTO.getEnd()
        )).thenReturn(expenseList);

        // Act
        LimitResponseDTO result = limitHandler.getLimitResponseDTO(limitDTO, user);

        // Assert
        assertEquals(category.getName(), result.getCategory());
        assertEquals(limitDTO.getExpenseLimit(), result.getLimit());
        assertEquals(2100.0, result.getCurrent(), 0.001);
        assertEquals("Your limit is exceeded by 1100.0", result.getRemaining());
        assertEquals("210.0 %", result.getPercentage());
        assertEquals("Your limit is exceeded by 110.0 %", result.getPercentageRemaining());
        assertEquals(limitDTO.getStart(), result.getStart());
        assertEquals(limitDTO.getEnd(), result.getEnd());
    }

}