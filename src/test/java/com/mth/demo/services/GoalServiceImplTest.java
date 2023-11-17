package com.mth.demo.services;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.mth.demo.exception.MissingNameException;
import com.mth.demo.exception.PfpException;
import com.mth.demo.exception.goalexceptions.BalanceLowException;
import com.mth.demo.exception.goalexceptions.DepositMissingException;
import com.mth.demo.exception.goalexceptions.GoalAlreadyExistException;
import com.mth.demo.exception.goalexceptions.GoalNotExistException;
import com.mth.demo.exception.goalexceptions.TargetMissingException;
import com.mth.demo.exception.goalexceptions.WithdrawalMissingException;
import com.mth.demo.models.dto.goal.GoalProgressResponseDTO;
import com.mth.demo.models.entities.finance.Goal;
import com.mth.demo.models.entities.user.RoleType;
import com.mth.demo.models.entities.user.User;
import com.mth.demo.repositories.GoalRepository;
import com.mth.demo.repositories.UserRepository;
import com.mth.demo.security.config.JwtService;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class GoalServiceImplTest {


  @Test
  public void createNewGoal_nameIsMissing_returnThrows() {
    // Arrange
    var goalRepositoryMock = mock(GoalRepository.class);
    var userRepositoryMock = mock(UserRepository.class);
    var jwtServiceMock = mock(JwtService.class);

    var goalService = new GoalServiceImpl(goalRepositoryMock, userRepositoryMock, jwtServiceMock);
    var goal = new Goal();
    var request = "request";

    // Act, Assert
    Assertions.assertThrows(MissingNameException.class,
        () -> goalService.createNewGoal(goal, request));
  }

  @Test
  public void createNewGoal_goalAlreadyExist_returnThrows() {
    // Arrange
    var goalRepositoryMock = mock(GoalRepository.class);
    var userRepositoryMock = mock(UserRepository.class);
    var jwtServiceMock = mock(JwtService.class);

    var goalService = new GoalServiceImpl(goalRepositoryMock, userRepositoryMock, jwtServiceMock);
    var user = new User("test", "test", "test@test.com", "123456", LocalDate.now(), "USER");
    var goal = new Goal("test", 16000.00, user);
    var request = "request";
    when(jwtServiceMock.extractUserNameFromHeader(request)).thenReturn(user.getEmail());
    when(userRepositoryMock.findByEmailOrderById(user.getEmail())).thenReturn(user);
    when(goalRepositoryMock.existsByNameAndUser(goal.getName(), user)).thenReturn(true);

    // Act, Assert
    Assertions.assertThrows(GoalAlreadyExistException.class,
        () -> goalService.createNewGoal(goal, request));
  }

  @Test
  public void createNewGoal_goalTargetNotPresent_returnThrows() {
    // Arrange
    var goalRepositoryMock = mock(GoalRepository.class);
    var userRepositoryMock = mock(UserRepository.class);
    var jwtServiceMock = mock(JwtService.class);

    var goalService = new GoalServiceImpl(goalRepositoryMock, userRepositoryMock, jwtServiceMock);
    var goal = new Goal("test", null, null);
    var request = "request";

    // Act, Assert
    Assertions.assertThrows(TargetMissingException.class,
        () -> goalService.createNewGoal(goal, request));
  }

  @Test
  public void createNewGoal_goalCreated_returnOfMessage() throws PfpException {
    // Arrange
    var goalRepositoryMock = mock(GoalRepository.class);
    var userRepositoryMock = mock(UserRepository.class);
    var jwtServiceMock = mock(JwtService.class);

    var goalService = new GoalServiceImpl(goalRepositoryMock, userRepositoryMock, jwtServiceMock);
    var goal = new Goal("test", 16000.00, null);
    var request = "request";
    // Act

    var result = goalService.createNewGoal(goal, request);

    // Assert
    Assertions.assertEquals(
        "Your goal " + goal.getName() + " was created with target: " + goal.getTargetGoal() + " !",
        result.getMessage());
  }

  @Test
  public void deposit_nameIsMissing_returnThrows() {
    // Arrange
    var goalRepositoryMock = mock(GoalRepository.class);
    var userRepositoryMock = mock(UserRepository.class);
    var jwtServiceMock = mock(JwtService.class);

    var goalService = new GoalServiceImpl(goalRepositoryMock, userRepositoryMock, jwtServiceMock);
    var goal = new Goal();
    var request = "request";

    // Act, Assert
    Assertions.assertThrows(MissingNameException.class,
        () -> goalService.deposit(goal, null, request));
  }

  @Test
  public void deposit_goalIsNotExist_returnThrows() {
    // Arrange
    var goalRepositoryMock = mock(GoalRepository.class);
    var userRepositoryMock = mock(UserRepository.class);
    var jwtServiceMock = mock(JwtService.class);

    var goalService = new GoalServiceImpl(goalRepositoryMock, userRepositoryMock, jwtServiceMock);
    var user =
        new User(1L, "test", "test", "test@test.com", "test", LocalDate.now(), RoleType.USER);
    var goal = new Goal("test", 10000.00, user);
    var request = "request";
    when(jwtServiceMock.extractUserNameFromHeader(request)).thenReturn(user.getEmail());
    when(userRepositoryMock.findByEmailOrderById(user.getEmail())).thenReturn(user);
    when(goalRepositoryMock.existsByNameAndUser(goal.getName(), null)).thenReturn(false);

    // Act, Assert
    Assertions.assertThrows(GoalNotExistException.class,
        () -> goalService.deposit(goal, 1000.00, request));
  }

  @Test
  public void deposit_depositIsNotPresent_returnThrows() {
    // Arrange
    var goalRepositoryMock = mock(GoalRepository.class);
    var userRepositoryMock = mock(UserRepository.class);
    var jwtServiceMock = mock(JwtService.class);

    var goalService = new GoalServiceImpl(goalRepositoryMock, userRepositoryMock, jwtServiceMock);
    var user = new User("test", "test", "test@test.com", "test", LocalDate.now(), "USER");
    var goal = new Goal("test", 10000.00, user);
    var request = "request";
    when(jwtServiceMock.extractUserNameFromHeader(request)).thenReturn(user.getEmail());
    when(userRepositoryMock.findByEmailOrderById(user.getEmail())).thenReturn(user);
    when(goalRepositoryMock.existsByNameAndUser(goal.getName(), user)).thenReturn(true);

    // Act, Assert
    Assertions.assertThrows(DepositMissingException.class,
        () -> goalService.deposit(goal, null, request));
  }

  @Test
  public void deposit_depositDone_returnMessage() throws PfpException {
    // Arrange
    var goalRepositoryMock = mock(GoalRepository.class);
    var userRepositoryMock = mock(UserRepository.class);
    var jwtServiceMock = mock(JwtService.class);

    var goalService = new GoalServiceImpl(goalRepositoryMock, userRepositoryMock, jwtServiceMock);
    var user = new User("test", "test", "test@test.com", "test", LocalDate.now(), "USER");
    var goal = new Goal("test", 10000.0, user);
    var depositedGoal = new Goal();
    var request = "request";
    when(jwtServiceMock.extractUserNameFromHeader(request)).thenReturn(user.getEmail());
    when(userRepositoryMock.findByEmailOrderById(user.getEmail())).thenReturn(user);
    when(goalRepositoryMock.existsByNameAndUser(goal.getName(), user)).thenReturn(true);
    when(goalRepositoryMock.findByNameAndUser(goal.getName(), user)).thenReturn(
        depositedGoal = goal);
    // Act
    var result = goalService.deposit(goal, 100.00, request);

    // Assert
    Assertions.assertEquals("You was deposit " + goal.getDeposit() + " to " + goal.getName() +
            " and your current balance is " + depositedGoal.getCurrentBalance() + " !",
        result.getMessage());
  }

  @Test
  public void deposit_targetReach_returnMessage() throws PfpException {
    // Arrange
    var goalRepositoryMock = mock(GoalRepository.class);
    var userRepositoryMock = mock(UserRepository.class);
    var jwtServiceMock = mock(JwtService.class);

    var goalService = new GoalServiceImpl(goalRepositoryMock, userRepositoryMock, jwtServiceMock);
    var user = new User("test", "test", "test@test.com", "test", LocalDate.now(), "USER");
    var goal = new Goal(1L, "test", 10000.0, 0.0, 0.0, 10000.00, user);
    var depositedGoal = new Goal();
    var request = "request";
    when(jwtServiceMock.extractUserNameFromHeader(request)).thenReturn(user.getEmail());
    when(userRepositoryMock.findByEmailOrderById(user.getEmail())).thenReturn(user);
    when(goalRepositoryMock.existsByNameAndUser(goal.getName(), user)).thenReturn(true);
    when(goalRepositoryMock.findByNameAndUser(goal.getName(), user)).thenReturn(
        depositedGoal = goal);

    // Act
    var result = goalService.deposit(goal, 1000.00, request);

    // Assert
    Assertions.assertEquals("You was reach your target of " + depositedGoal.getName() + "!",
        result.getMessage());
  }

  @Test
  public void withdrawal_nameIsMissing_returnThrows() {
    // Arrange
    var goalRepositoryMock = mock(GoalRepository.class);
    var userRepositoryMock = mock(UserRepository.class);
    var jwtServiceMock = mock(JwtService.class);

    var goalService = new GoalServiceImpl(goalRepositoryMock, userRepositoryMock, jwtServiceMock);
    var goal = new Goal();
    var request = "request";

    // Act, Assert
    Assertions.assertThrows(MissingNameException.class,
        () -> goalService.withdrawal(goal, 1000.00, request));
  }

  @Test
  public void withdrawal_goalIsNotExist_returnThrows() {
    // Arrange
    var goalRepositoryMock = mock(GoalRepository.class);
    var userRepositoryMock = mock(UserRepository.class);
    var jwtServiceMock = mock(JwtService.class);

    var goalService = new GoalServiceImpl(goalRepositoryMock, userRepositoryMock, jwtServiceMock);
    var user = new User("test", "test", "test@test.com", "test", LocalDate.now(), "USER");
    var goal = new Goal("test", 10000.00, user);
    var request = "request";
    when(jwtServiceMock.extractUserNameFromHeader(request)).thenReturn(user.getEmail());
    when(userRepositoryMock.findByEmailOrderById(user.getEmail())).thenReturn(user);
    when(goalRepositoryMock.existsByNameAndUser(goal.getName(), null)).thenReturn(true);

    // Act, Assert
    Assertions.assertThrows(GoalNotExistException.class,
        () -> goalService.withdrawal(goal, 10.00, request));
  }

  @Test
  public void withdrawal_withdrawalIsNotPresent_returnThrows() {
    // Arrange
    var goalRepositoryMock = mock(GoalRepository.class);
    var userRepositoryMock = mock(UserRepository.class);
    var jwtServiceMock = mock(JwtService.class);

    var goalService = new GoalServiceImpl(goalRepositoryMock, userRepositoryMock, jwtServiceMock);
    var user = new User("test", "test", "test@test.com", "test", LocalDate.now(), "USER");
    var goal = new Goal("test", 10000.00, user);
    var request = "request";
    when(jwtServiceMock.extractUserNameFromHeader(request)).thenReturn(user.getEmail());
    when(userRepositoryMock.findByEmailOrderById(user.getEmail())).thenReturn(user);
    when(goalRepositoryMock.existsByNameAndUser(goal.getName(), user)).thenReturn(true);

    // Act, Assert
    Assertions.assertThrows(WithdrawalMissingException.class,
        () -> goalService.withdrawal(goal, null, request));
  }

  @Test
  public void withdrawal_withdrawalDone_returnMessage() throws PfpException {
    // Arrange
    var goalRepositoryMock = mock(GoalRepository.class);
    var userRepositoryMock = mock(UserRepository.class);
    var jwtServiceMock = mock(JwtService.class);

    var goalService = new GoalServiceImpl(goalRepositoryMock, userRepositoryMock, jwtServiceMock);
    var user = new User("test", "test", "test@test.com", "test", LocalDate.now(), "USER");
    var goal = new Goal(1L, "test", 10000.00, 0.00, 0.00, 1000.00, user);
    var withdrawedGoal = new Goal();
    var request = "request";
    when(jwtServiceMock.extractUserNameFromHeader(request)).thenReturn(user.getEmail());
    when(userRepositoryMock.findByEmailOrderById(user.getEmail())).thenReturn(user);
    when(goalRepositoryMock.existsByNameAndUser(goal.getName(), user)).thenReturn(true);
    when(goalRepositoryMock.findByNameAndUser(goal.getName(), user)).thenReturn(
        withdrawedGoal = goal);

    // Act
    var result = goalService.withdrawal(goal, 150.00, request);

    //Assert
    Assertions.assertEquals(
        "You was withdrawal " + goal.getWithdrawal() + " from " + goal.getName() +
            " and your current balance is " + withdrawedGoal.getCurrentBalance() + " !",
        result.getMessage());
  }

  @Test
  public void withdrawal_lowBalance_returnThrows() {
    // Arrange
    var goalRepositoryMock = mock(GoalRepository.class);
    var userRepositoryMock = mock(UserRepository.class);
    var jwtServiceMock = mock(JwtService.class);

    var goalService = new GoalServiceImpl(goalRepositoryMock, userRepositoryMock, jwtServiceMock);
    var user = new User("test", "test", "test@test.com", "test", LocalDate.now(), "USER");
    var goal = new Goal("test", 10000.00, user);
    var request = "request";
    when(jwtServiceMock.extractUserNameFromHeader(request)).thenReturn(user.getEmail());
    when(userRepositoryMock.findByEmailOrderById(user.getEmail())).thenReturn(user);
    when(goalRepositoryMock.existsByNameAndUser(goal.getName(), user)).thenReturn(true);
    when(goalRepositoryMock.findByNameAndUser(goal.getName(), user)).thenReturn(goal);

    // Act, Assert
    Assertions.assertThrows(BalanceLowException.class,
        () -> goalService.withdrawal(goal, 150.00, request));
  }

  @Test
  public void checkProgress_nameIsMissing_returnThrows() {
    // Arrange
    var goalRepositoryMock = mock(GoalRepository.class);
    var userRepositoryMock = mock(UserRepository.class);
    var jwtServiceMock = mock(JwtService.class);

    var goalService = new GoalServiceImpl(goalRepositoryMock, userRepositoryMock, jwtServiceMock);
    var goal = new Goal();
    var request = "request";

    // Act, Assert
    Assertions.assertThrows(MissingNameException.class,
        () -> goalService.checkProgress(goal, request));
  }

  @Test
  public void checkProgress_goalIsNotExist_returnThrows() {
    // Arrange
    var goalRepositoryMock = mock(GoalRepository.class);
    var userRepositoryMock = mock(UserRepository.class);
    var jwtServiceMock = mock(JwtService.class);

    var goalService = new GoalServiceImpl(goalRepositoryMock, userRepositoryMock, jwtServiceMock);
    var user = new User("test", "test", "test@test.com", "test", LocalDate.now(), "USER");
    var goal = new Goal("test", 10000.00, user);
    var request = "request";
    when(jwtServiceMock.extractUserNameFromHeader(request)).thenReturn(user.getEmail());
    when(userRepositoryMock.findByEmailOrderById(user.getEmail())).thenReturn(user);
    when(goalRepositoryMock.existsByNameAndUser(goal.getName(), null)).thenReturn(true);
    when(goalRepositoryMock.findByNameAndUser(goal.getName(), user)).thenReturn(goal);

    // Act, Assert
    Assertions.assertThrows(GoalNotExistException.class,
        () -> goalService.checkProgress(goal, request));
  }

  @Test
  public void checkProgress_printProgress_jsonValue() throws PfpException {
    // Arrange
    var goalRepositoryMock = mock(GoalRepository.class);
    var userRepositoryMock = mock(UserRepository.class);
    var jwtServiceMock = mock(JwtService.class);

    var goalService = new GoalServiceImpl(goalRepositoryMock, userRepositoryMock, jwtServiceMock);
    var user = new User("test", "test", "test@test.com", "test", LocalDate.now(), "USER");
    var goal = new Goal(1L, "test", 10000.00, 0.00, 0.00, 2500.00, user);
    var goalCurrent = new Goal();
    var request = "request";

    when(jwtServiceMock.extractUserNameFromHeader(request)).thenReturn(user.getEmail());
    when(userRepositoryMock.findByEmailOrderById(user.getEmail())).thenReturn(user);
    when(goalRepositoryMock.existsByNameAndUser(goal.getName(), user)).thenReturn(true);
    when(goalRepositoryMock.findByNameAndUser(goal.getName(), user)).thenReturn(goalCurrent = goal);

    // Act
    var result = goalService.checkProgress(goal, request);

    // Assert
    Assertions.assertEquals(GoalProgressResponseDTO.builder().name(goalCurrent.getName())
        .currentBalance(goalCurrent.getCurrentBalance()).targetGoal(goalCurrent.getTargetGoal())
        .lastDeposit(goalCurrent.getDeposit()).lastWithdrawal(goalCurrent.getWithdrawal())
        .percentage(percentage(goalCurrent)).build(), result);
  }

  private Integer percentage(Goal goalCurrent) {
    if (goalCurrent.getCurrentBalance() == null) {
      return 0;
    }
    double formatted = 100 * (goalCurrent.getCurrentBalance() / goalCurrent.getTargetGoal());
    return (int) formatted;
  }

  @Test
  public void showAllGoal_showGoals_jsonValue() {
    // Arrange
    var goalRepositoryMock = mock(GoalRepository.class);
    var userRepositoryMock = mock(UserRepository.class);
    var jwtServiceMock = mock(JwtService.class);

//    var goalService = new GoalServiceImpl(goalRepositoryMock, userRepositoryMock, jwtServiceMock);
    var user1 =
        new User(1L, "test", "test", "test@test.com", "test", LocalDate.now(), RoleType.USER);
    var goal1 = new Goal(1L, "test1", 1000.00, 0.00, 0.00, 250.00, user1);
    var goal2 = new Goal(2L, "test1", 1000.00, 0.00, 0.00, 250.00, user1);
    var goal3 = new Goal(3L, "test1", 1000.00, 0.00, 0.00, 250.00, user1);
    var request = "request";

    when(jwtServiceMock.extractUserNameFromHeader(request)).thenReturn(user1.getEmail());
    when(userRepositoryMock.findByEmailOrderById(user1.getEmail())).thenReturn(user1);

    // Act
    goalRepositoryMock.saveAll(List.of(goal1, goal2, goal3));
    when(goalRepositoryMock.existsByNameAndUser(goal1.getName(), user1)).thenReturn(true);
    when(goalRepositoryMock.existsByNameAndUser(goal2.getName(), user1)).thenReturn(true);
    when(goalRepositoryMock.existsByNameAndUser(goal3.getName(), user1)).thenReturn(true);


    // Assert
    Assertions.assertTrue(goalRepositoryMock.existsByNameAndUser(goal1.getName(), user1));
    Assertions.assertTrue(goalRepositoryMock.existsByNameAndUser(goal2.getName(), user1));
    Assertions.assertTrue(goalRepositoryMock.existsByNameAndUser(goal3.getName(), user1));
  }

  @Test
  public void deleteGoal_nameIsMissing_returnThrows() {
    // Arrange
    var goalRepositoryMock = mock(GoalRepository.class);
    var userRepositoryMock = mock(UserRepository.class);
    var jwtServiceMock = mock(JwtService.class);

    var goalService = new GoalServiceImpl(goalRepositoryMock, userRepositoryMock, jwtServiceMock);
    var goal = new Goal();
    var request = "request";

    // Act, Assert
    Assertions.assertThrows(MissingNameException.class,
        () -> goalService.deleteGoal(goal, request));
  }

  @Test
  public void deleteGoal_goalIsNotExist_returnThrows() {
    // Arrange
    var goalRepositoryMock = mock(GoalRepository.class);
    var userRepositoryMock = mock(UserRepository.class);
    var jwtServiceMock = mock(JwtService.class);

    var goalService = new GoalServiceImpl(goalRepositoryMock, userRepositoryMock, jwtServiceMock);
    var user = new User("test", "test", "test@test.com", "test", LocalDate.now(), "USER");
    var goal = new Goal("test", 10000.00, user);
    var request = "request";
    when(jwtServiceMock.extractUserNameFromHeader(request)).thenReturn(user.getEmail());
    when(userRepositoryMock.findByEmailOrderById(user.getEmail())).thenReturn(user);
    when(goalRepositoryMock.existsByNameAndUser(goal.getName(), null)).thenReturn(true);
    when(goalRepositoryMock.findByNameAndUser(goal.getName(), user)).thenReturn(goal);

    // Act, Assert
    Assertions.assertThrows(GoalNotExistException.class,
        () -> goalService.deleteGoal(goal, request));
  }

  @Test
  public void deleteGoal_goalDeleted_returnMessage() throws PfpException {
    // Arrange
    var goalRepositoryMock = mock(GoalRepository.class);
    var userRepositoryMock = mock(UserRepository.class);
    var jwtServiceMock = mock(JwtService.class);

    var goalService = new GoalServiceImpl(goalRepositoryMock, userRepositoryMock, jwtServiceMock);
    var user = new User("test", "test", "test@test.com", "test", LocalDate.now(), "USER");
    var goal = new Goal("test", 10000.00, user);
    var deletedGoal = new Goal();
    var request = "request";
    when(jwtServiceMock.extractUserNameFromHeader(request)).thenReturn(user.getEmail());
    when(userRepositoryMock.findByEmailOrderById(user.getEmail())).thenReturn(user);
    when(goalRepositoryMock.existsByNameAndUser(goal.getName(), user)).thenReturn(true);
    when(goalRepositoryMock.findByNameAndUser(goal.getName(), user)).thenReturn(deletedGoal = goal);

    // Act
    var result = goalService.deleteGoal(goal, request);

    // Assert
    Assertions.assertEquals(deletedGoal.getName() + " was deleted from you Financial Goals!",
        result.getMessage());
  }

  @Test
  public void editGoal_nameIsMissing_returnThrows() {
    // Arrange
    var goalRepositoryMock = mock(GoalRepository.class);
    var userRepositoryMock = mock(UserRepository.class);
    var jwtServiceMock = mock(JwtService.class);

    var goalService = new GoalServiceImpl(goalRepositoryMock, userRepositoryMock, jwtServiceMock);
    var goal = new Goal();
    var request = "request";

    // Act, Assert
    Assertions.assertThrows(MissingNameException.class,
        () -> goalService.editGoal(goal.getName(), goal, request));
  }

  @Test
  public void editGoal_goalIsNotExist_returnThrows() {
    // Arrange
    var goalRepositoryMock = mock(GoalRepository.class);
    var userRepositoryMock = mock(UserRepository.class);
    var jwtServiceMock = mock(JwtService.class);

    var goalService = new GoalServiceImpl(goalRepositoryMock, userRepositoryMock, jwtServiceMock);
    var user = new User("test", "test", "test@test.com", "test", LocalDate.now(), "USER");
    var goal = new Goal("test", 10000.00, user);
    var request = "request";
    when(jwtServiceMock.extractUserNameFromHeader(request)).thenReturn(user.getEmail());
    when(userRepositoryMock.findByEmailOrderById(user.getEmail())).thenReturn(user);
    when(goalRepositoryMock.existsByNameAndUser(goal.getName(), null)).thenReturn(true);
    when(goalRepositoryMock.findByNameAndUser(goal.getName(), user)).thenReturn(goal);

    // Act, Assert
    Assertions.assertThrows(GoalNotExistException.class,
        () -> goalService.editGoal(goal.getName(), goal, request));
  }

  @Test
  public void editGoal_editDone_returnMessage() throws PfpException {
    // Arrange
    var goalRepositoryMock = mock(GoalRepository.class);
    var userRepositoryMock = mock(UserRepository.class);
    var jwtServiceMock = mock(JwtService.class);

    var goalService = new GoalServiceImpl(goalRepositoryMock, userRepositoryMock, jwtServiceMock);
    var user = new User("test", "test", "test@test.com", "test", LocalDate.now(), "USER");
    var goal = new Goal("test", 10000.00, user);
    var editedGoal = new Goal();
    var name = goal.getName();
    var request = "request";
    when(jwtServiceMock.extractUserNameFromHeader(request)).thenReturn(user.getEmail());
    when(userRepositoryMock.findByEmailOrderById(user.getEmail())).thenReturn(user);
    when(goalRepositoryMock.existsByNameAndUser(goal.getName(), user)).thenReturn(true);
    when(goalRepositoryMock.findByNameAndUser(goal.getName(), user)).thenReturn(editedGoal = goal);

    // Act
    var result = goalService.editGoal(name, goal, request);

    // Assert
    Assertions.assertEquals(name + " was edited to " + editedGoal.getName() + ", with target: " +
        editedGoal.getTargetGoal(), result.getMessage());
  }
}