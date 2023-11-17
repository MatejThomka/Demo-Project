package com.mth.demo.services;

import com.mth.demo.exception.MissingNameException;
import com.mth.demo.exception.NothingInsideException;
import com.mth.demo.exception.PfpException;
import com.mth.demo.exception.goalexceptions.BalanceLowException;
import com.mth.demo.exception.goalexceptions.DepositMissingException;
import com.mth.demo.exception.goalexceptions.GoalAlreadyExistException;
import com.mth.demo.exception.goalexceptions.GoalNotExistException;
import com.mth.demo.exception.goalexceptions.NothingToChangeException;
import com.mth.demo.exception.goalexceptions.TargetMissingException;
import com.mth.demo.exception.goalexceptions.WithdrawalMissingException;
import com.mth.demo.models.dto.goal.GoalProgressResponseDTO;
import com.mth.demo.models.dto.MessageDTO;
import com.mth.demo.models.entities.finance.Goal;
import com.mth.demo.models.entities.user.User;
import com.mth.demo.repositories.GoalRepository;
import com.mth.demo.repositories.UserRepository;
import com.mth.demo.security.config.JwtService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GoalServiceImpl implements GoalService {

  private final GoalRepository goalRepository;
  private final UserRepository userRepository;
  private final JwtService jwtService;

  @Autowired
  public GoalServiceImpl(GoalRepository goalRepository, UserRepository userRepository,
                         JwtService jwtService) {
    this.goalRepository = goalRepository;
    this.userRepository = userRepository;
    this.jwtService = jwtService;
  }

  @Override
  public MessageDTO createNewGoal(Goal goal, String request) throws PfpException {

    User user = userRepository.findByEmailOrderById(jwtService.extractUserNameFromHeader(request));

    if (goalRepository.existsByNameAndUser(goal.getName(), user)) {
      throw new GoalAlreadyExistException();
    }

    checkName(goal.getName());

    if (goal.getTargetGoal() == null) {
      throw new TargetMissingException();
    }

    Goal newGoal = new Goal(goal.getName(), goal.getTargetGoal(), user);
    goalRepository.save(newGoal);

    return MessageDTO.builder().message(
            "Your goal " + goal.getName() + " was created with target: " + goal.getTargetGoal() + " !")
        .build();
  }

  @Override
  public MessageDTO deposit(Goal goal, Double deposit, String request) throws PfpException {
    User user = userRepository.findByEmailOrderById(jwtService.extractUserNameFromHeader(request));
    checkName(goal.getName());
    checkExist(goal.getName(), user);
    if (deposit == null) {
      throw new DepositMissingException();
    }
    Goal depositedGoal = goalRepository.findByNameAndUser(goal.getName(), user);
    if (depositedGoal.getCurrentBalance() >= depositedGoal.getTargetGoal()) {
      return MessageDTO.builder()
          .message("You was reach your target of " + depositedGoal.getName() + "!").build();
    }
    depositedGoal.setDeposit(deposit);
    depositedGoal.setCurrentBalance(
        Math.round((depositedGoal.getCurrentBalance() + deposit) * 100.0) / 100.0);
    goalRepository.save(depositedGoal);
    return MessageDTO.builder().message(
        "You was deposit " + goal.getDeposit() + " to " + goal.getName() +
            " and your current balance is " + depositedGoal.getCurrentBalance() + " !").build();
  }

  @Override
  public MessageDTO withdrawal(Goal goal, Double withdrawal, String request) throws PfpException {
    User user = userRepository.findByEmailOrderById(jwtService.extractUserNameFromHeader(request));
    checkName(goal.getName());
    checkExist(goal.getName(), user);
    if (withdrawal == null) {
      throw new WithdrawalMissingException();
    }
    Goal withdrawedGoal = goalRepository.findByNameAndUser(goal.getName(), user);
    if (withdrawedGoal.getCurrentBalance() <= 0 ||
        withdrawal > withdrawedGoal.getCurrentBalance()) {
      throw new BalanceLowException();
    }
    withdrawedGoal.setWithdrawal(withdrawal);
    withdrawedGoal.setCurrentBalance(
        Math.round((withdrawedGoal.getCurrentBalance() - withdrawal) * 100.0) / 100.0);
    goalRepository.save(withdrawedGoal);
    return MessageDTO.builder().message(
        "You was withdrawal " + goal.getWithdrawal() + " from " + goal.getName() +
            " and your current balance is " + withdrawedGoal.getCurrentBalance() + " !").build();
  }

  @Override
  public Integer percentage(Goal goal, User user) {
    Goal progressGoal = goalRepository.findByNameAndUser(goal.getName(), user);
    if (goal.getCurrentBalance() == null) {
      return 0;
    }
    double formatted = 100 * (progressGoal.getCurrentBalance() / progressGoal.getTargetGoal());
    return (int) formatted;
  }

  @Override
  public GoalProgressResponseDTO checkProgress(Goal goal, String request) throws PfpException {
    User user = userRepository.findByEmailOrderById(jwtService.extractUserNameFromHeader(request));
    checkName(goal.getName());
    checkExist(goal.getName(), user);
    var goalCurrent = goalRepository.findByNameAndUser(goal.getName(), user);
    return GoalProgressResponseDTO.builder().name(goalCurrent.getName())
        .currentBalance(goalCurrent.getCurrentBalance()).targetGoal(goalCurrent.getTargetGoal())
        .lastDeposit(goalCurrent.getDeposit()).lastWithdrawal(goalCurrent.getWithdrawal())
        .percentage(percentage(goalCurrent, user)).build();
  }

  @Override
  public List<GoalProgressResponseDTO> showAllGoal(String request) throws PfpException {
    User user = userRepository.findByEmailOrderById(jwtService.extractUserNameFromHeader(request));
    List<GoalProgressResponseDTO> goals = new ArrayList<>();
    List<Goal> userGoals = goalRepository.findAllByUser(user);
    if (userGoals.isEmpty()) {
      throw new NothingInsideException();
    }
    for (Goal goal : userGoals) {
      goals.add(GoalProgressResponseDTO.builder()
          .name(goal.getName())
          .targetGoal(goal.getTargetGoal())
          .currentBalance(goal.getCurrentBalance())
          .build());
    }
    return goals;
  }

  @Override
  public MessageDTO deleteGoal(Goal goal, String request) throws PfpException {
    User user = userRepository.findByEmailOrderById(jwtService.extractUserNameFromHeader(request));
    checkName(goal.getName());
    checkExist(goal.getName(), user);
    Goal deletedGoal = goalRepository.findByNameAndUser(goal.getName(), user);
    goalRepository.delete(deletedGoal);
    return MessageDTO.builder()
        .message(deletedGoal.getName() + " was deleted from you Financial Goals!").build();
  }

  @Override
  public MessageDTO editGoal(String name, Goal goal, String request) throws PfpException {
    User user = userRepository.findByEmailOrderById(jwtService.extractUserNameFromHeader(request));
    checkName(name);
    checkExist(name, user);
    Goal editedGoal = goalRepository.findByNameAndUser(name, user);
    if (goal.getName() == null && goal.getTargetGoal() == null) {
      throw new NothingToChangeException();
    }

    if (goal.getName() == null) {
      editedGoal.setName(name);
    } else {
      editedGoal.setName(goal.getName());
    }

    if (goal.getTargetGoal() == null) {
      editedGoal.setTargetGoal(editedGoal.getTargetGoal());
    } else {
      editedGoal.setTargetGoal(goal.getTargetGoal());
    }

    goalRepository.save(editedGoal);
    return MessageDTO.builder().message(
        name + " was edited to " + editedGoal.getName() + ", with target: " +
            editedGoal.getTargetGoal()).build();
  }

  @Override
  public void checkName(String name) throws PfpException {
    if (name == null) {
      throw new MissingNameException();
    }
  }

  @Override
  public void checkExist(String name, User user) throws PfpException {
    if (!goalRepository.existsByNameAndUser(name, user)) {
      throw new GoalNotExistException();
    }
  }
}
