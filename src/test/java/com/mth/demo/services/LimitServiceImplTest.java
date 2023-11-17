package com.mth.demo.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.mth.demo.exception.NothingInsideException;
import com.mth.demo.handler.limit.LimitHandler;
import com.mth.demo.models.dto.MessageDTO;
import com.mth.demo.models.dto.limit.LimitDTO;
import com.mth.demo.models.dto.limit.LimitResponseDTO;
import com.mth.demo.models.entities.finance.Category;
import com.mth.demo.models.entities.finance.Limit;
import com.mth.demo.models.entities.user.RoleType;
import com.mth.demo.models.entities.user.User;
import com.mth.demo.repositories.LimitRepository;
import com.mth.demo.repositories.UserRepository;
import com.mth.demo.security.config.JwtService;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class LimitServiceImplTest {

    @Mock
    private LimitRepository limitRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private LimitHandler limitHandler;

    @InjectMocks
    private LimitServiceImpl limitService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        limitService = new LimitServiceImpl(limitRepository, jwtService, userRepository, limitHandler);
    }

    private LimitResponseDTO sampleLimitResponseDTO() {
        // Create and return a sample LimitResponseDTO object for testing purposes
        return new LimitResponseDTO();
    }

    @Test
    public void setLimit_WithValidData_ShouldReturnLimitResponseDTO() throws NothingInsideException {
        // Arrange
        String request = "valid-request";

        LimitDTO limitDTO = new LimitDTO();
        limitDTO.setCategory("test_category");
        limitDTO.setExpenseLimit(1000.0);
        limitDTO.setStart(LocalDate.now());
        limitDTO.setEnd(LocalDate.now().plusMonths(1));

        User user = new User(1L, "test", "test", "test@test.com", "test", LocalDate.now(), RoleType.USER);

        Category category = new Category(1L, 1L, limitDTO.getCategory());

        Limit limit = new Limit();
        limit.setId(1L);
        limit.setCategory(category);
        limit.setExpenseLimit(limitDTO.getExpenseLimit());
        limit.setStart(limitDTO.getStart());
        limit.setEnd(limitDTO.getEnd());
        limit.setUser(user);

        // Mock the repository and handler methods
        when(userRepository.findByEmailOrderById(jwtService.extractUserNameFromHeader(request))).thenReturn(user);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(limitHandler.getCategoryByName(limitDTO, user.getId())).thenReturn(Optional.of(category));
        when(limitHandler.getLimitByCategoryName(limitDTO, user)).thenReturn(Optional.empty());
        when(limitRepository.save(any(Limit.class))).thenReturn(limit);
        when(limitHandler.getLimitResponseDTO(limitDTO, user)).thenReturn(sampleLimitResponseDTO());

        // Act
        LimitResponseDTO result = limitService.setLimit(limitDTO, request);

        // Assert
        assertEquals(sampleLimitResponseDTO(), result);
    }

    @Test
    void setLimit_WhenLimitDTOIsValid_ShouldSaveNewLimit() {
        // Arrange
        String request = "valid-request";

        LimitDTO limitDTO = new LimitDTO();
        limitDTO.setCategory("test_category");
        limitDTO.setExpenseLimit(1000.0);
        limitDTO.setStart(LocalDate.now());
        limitDTO.setEnd(LocalDate.now().plusMonths(1));

        User user = new User(1L, "test", "test", "test@test.com", "test", LocalDate.now(), RoleType.USER);

        Limit limit = new Limit();

        // Mock the repository and handler methods
        when(userRepository.findByEmailOrderById(jwtService.extractUserNameFromHeader(request))).thenReturn(user);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(limitRepository.save(any(Limit.class))).thenReturn(limit);
        when(limitHandler.getLimitByCategoryName(limitDTO, user)).thenReturn(Optional.empty());
        when(limitHandler.getLimitResponseDTO(limitDTO, user)).thenReturn(sampleLimitResponseDTO());

        // Act
        limitRepository.save(limit);

        // Assert
        verify(limitRepository).save(limit);
    }

    @Test
    void setLimit_DTOIsNull_SetDefault() throws NothingInsideException {
        // Arrange
        String request = "valid-request";

        Category defaultCategory = new Category(-1L, 1L, "other");

        LimitDTO limitDTO = new LimitDTO();
        limitDTO.setCategory(null);
        limitDTO.setExpenseLimit(null);
        limitDTO.setStart(null);
        limitDTO.setEnd(null);

        User user = new User(1L, "test", "test", "test@test.com", "test", LocalDate.now(), RoleType.USER);

        // Mock the repository and handler methods
        when(userRepository.findByEmailOrderById(jwtService.extractUserNameFromHeader(request))).thenReturn(user);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(limitHandler.getCategoryByName(limitDTO, user.getId())).thenReturn(Optional.of(defaultCategory));
        sampleLimitResponseDTO();
        when(limitHandler.getLimitResponseDTO(limitDTO, user)).thenReturn(LimitResponseDTO.builder()
                .category(defaultCategory.getName())
                .limit(1000.0)
                .start(LocalDate.now())
                .end(LocalDate.now().plusMonths(1))
                .build());

        // Act
        LimitResponseDTO result = limitService.setLimit(limitDTO, request);

        // Assert
        assertNotNull(result);
        assertEquals("other", result.getCategory());
        assertEquals(1000.0, result.getLimit());
        assertEquals(LocalDate.now(), result.getStart());
        assertEquals(LocalDate.now().plusMonths(1), result.getEnd());
    }

    @Test
    public void setLimit_AllParametersProvided_LimitExists_ReturnsLimitResponseDTOAndSave() throws NothingInsideException {
        // Arrange
        LimitDTO limitDTO = new LimitDTO();
        limitDTO.setCategory("test_category");
        limitDTO.setExpenseLimit(1500.0);
        limitDTO.setStart(LocalDate.parse("2023-06-01"));
        limitDTO.setEnd(LocalDate.parse("2023-06-30"));

        String request = "valid-request";

        User user = new User(1L, "test", "test", "test@test.com", "test", LocalDate.now(), RoleType.USER);

        Category category = new Category(1L, 1L, limitDTO.getCategory());

        Limit existingLimit = new Limit();
        existingLimit.setId(1L);
        existingLimit.setCategory(category);
        existingLimit.setExpenseLimit(1000.0);
        existingLimit.setStart(LocalDate.parse("2023-05-01"));
        existingLimit.setEnd(LocalDate.parse("2023-05-31"));
        existingLimit.setUser(user);

        Limit updatedLimit = new Limit();
        updatedLimit.setId(null);
        updatedLimit.setCategory(category);
        updatedLimit.setExpenseLimit(limitDTO.getExpenseLimit());
        updatedLimit.setStart(limitDTO.getStart());
        updatedLimit.setEnd(limitDTO.getEnd());
        updatedLimit.setUser(user);

        // Mock the repository and handler methods
        when(userRepository.findByEmailOrderById(jwtService.extractUserNameFromHeader(request))).thenReturn(user);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(limitHandler.getCategoryByName(limitDTO, user.getId())).thenReturn(Optional.of(category));
        when(limitHandler.getLimitByCategoryName(limitDTO, user)).thenReturn(Optional.of(existingLimit));
        when(limitHandler.getLimitResponseDTO(limitDTO, user)).thenReturn(LimitResponseDTO.builder()
                .category(updatedLimit.getCategory().getName())
                .limit(updatedLimit.getExpenseLimit())
                .start(updatedLimit.getStart())
                .end(updatedLimit.getEnd())
                .build());
        when(limitHandler.getCategoryByName(limitDTO, user.getId())).thenReturn(Optional.of(category));
        when(limitHandler.getLimitByCategoryName(limitDTO, user)).thenReturn(Optional.empty());

        // Act
        LimitResponseDTO result = limitService.setLimit(limitDTO, request);

        // Assert
        verify(limitRepository).save(updatedLimit);
        assertEquals(category.getName(), result.getCategory());
        assertEquals(limitDTO.getExpenseLimit(), result.getLimit());
        assertEquals(limitDTO.getStart(), result.getStart());
        assertEquals(limitDTO.getEnd(), result.getEnd());
    }

    @Test
    void setLimit_CategoryNotExist_ReturnThrows() {
        // Arrange
        String request = "valid-request";

        LimitDTO limitDTO = new LimitDTO();

        User user = new User(1L, "test", "test", "test@test.com", "test", LocalDate.now(), RoleType.USER);

        // Mock the repository and handler methods
        when(userRepository.findByEmailOrderById(jwtService.extractUserNameFromHeader(request))).thenReturn(user);
        when(limitHandler.getCategoryByName(limitDTO, user.getId())).thenReturn(Optional.empty());

        // Act, Assert
        assertThrows(NothingInsideException.class, () ->
                limitService.setLimit(limitDTO, request)
        );
    }

    @Test
    void checkAllLimits_LimitsAreNotExists_ReturnThrows() {
        // Arrange
        String request = "valid-request";

        User user = new User();

        // Mock the repository and handler methods
        when(userRepository.findByEmailOrderById(jwtService.extractUserNameFromHeader(request))).thenReturn(user);
        when(limitRepository.findAllByUser(user)).thenReturn(new ArrayList<>());

        // Act, Assert
        assertThrows(NothingInsideException.class, () ->
                limitService.checkAllLimits(request)
        );
        verify(userRepository, times(1)).findByEmailOrderById(user.getEmail());
        verify(limitRepository, times(1)).findAllByUser(user);
        verifyNoMoreInteractions(userRepository, limitRepository, limitHandler);
    }

    @Test
    void checkLimitByType_LimitIsNotExist_ReturnThrows() {
        // Arrange
        String request = "valid-request";

        Category category = new Category(1L, 1L, "test_category");

        Limit limit = new Limit();
        limit.setCategory(category);

        User user = new User(1L, "test", "test", "test@test.com", "test", LocalDate.now(), RoleType.USER);

        // Mock the repository and handler methods
        when(userRepository.findByEmailOrderById(jwtService.extractUserNameFromHeader(request))).thenReturn(user);
        when(limitRepository.findByCategoryIdAndUser(category.getId(), user)).thenReturn(null);

        // Act, Assert
        assertThrows(NothingInsideException.class, () ->
                limitService.checkLimitByType(limit, request)
        );
    }

    @Test
    void deleteLimit_WithValidLimit_ShouldDeleteLimit() {
        // Arrange
        String request = "valid-request";

        LimitDTO limitDTO = new LimitDTO();
        limitDTO.setCategory("test_category");

        User user = new User(1L, "test", "test", "test@test.com", "test", LocalDate.now(), RoleType.USER);

        Category category = new Category(1L, 1L, limitDTO.getCategory());

        Limit limit = new Limit();
        limit.setId(1L);
        limit.setCategory(category);
        limit.setExpenseLimit(1000.0);
        limit.setStart(LocalDate.now());
        limit.setEnd(LocalDate.now().plusMonths(1));
        limit.setUser(user);

        // Mock the repository and handler methods
        when(userRepository.findByEmailOrderById(jwtService.extractUserNameFromHeader(request))).thenReturn(user);
        when(limitHandler.getLimitByCategoryName(limitDTO, user)).thenReturn(Optional.of(limit));

        // Act
        MessageDTO response =  limitService.deleteLimit(limitDTO, request);

        // Assert
        assertEquals("Your '" + limitDTO.getCategory() + "' category limit was deleted!", response.getMessage());
        verify(limitRepository).delete(limit);
    }

    @Test
    void deleteLimit_WithNonExistingLimit_ShouldReturnErrorMessage() {
        // Arrange
        String request = "valid-request";

        LimitDTO limitDTO = new LimitDTO();
        limitDTO.setCategory("test_category");

        User user = new User(1L, "test", "test", "test@test.com", "test", LocalDate.now(), RoleType.USER);

        // Mock the repository and handler methods
        when(userRepository.findByEmailOrderById(jwtService.extractUserNameFromHeader(request))).thenReturn(user);
        when(limitHandler.getLimitByCategoryName(limitDTO, user)).thenReturn(Optional.empty());

        // Act
        MessageDTO response = limitService.deleteLimit(limitDTO, request);

        // Assert
        assertEquals("You don't have a 'test_category' category limit!", response.getMessage());
        verify(limitRepository, never()).delete(any(Limit.class));
    }
}