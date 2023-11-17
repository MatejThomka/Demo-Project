package com.mth.demo.services;

import com.mth.demo.exception.PfpException;
import com.mth.demo.models.dto.MessageDTO;
import com.mth.demo.models.dto.goal.GoalProgressResponseDTO;
import com.mth.demo.models.entities.finance.Goal;
import com.mth.demo.models.entities.user.User;
import java.util.List;

public interface GoalService {
  MessageDTO createNewGoal(Goal goal, String request) throws PfpException;

  MessageDTO deposit(Goal goal, Double deposit, String request) throws PfpException;

  MessageDTO withdrawal(Goal goal, Double withdrawal, String request) throws PfpException;

  Integer percentage(Goal goal, User user);

  GoalProgressResponseDTO checkProgress(Goal goal, String request) throws PfpException;

  List<GoalProgressResponseDTO> showAllGoal(String request) throws PfpException;

  MessageDTO deleteGoal(Goal goal, String request) throws PfpException;

  MessageDTO editGoal(String name, Goal goal, String request) throws PfpException;

  void checkName(String name) throws PfpException;

  void checkExist(String name, User user) throws PfpException;
}
