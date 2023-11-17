package com.mth.demo.controllers;

import com.mth.demo.exception.PfpException;
import com.mth.demo.models.dto.MessageDTO;
import com.mth.demo.models.dto.goal.GoalProgressResponseDTO;
import com.mth.demo.models.entities.finance.Goal;
import com.mth.demo.services.GoalService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/goal")
@RequiredArgsConstructor
public class GoalApiController {

  private final GoalService goalService;

  @PostMapping("/create")
  ResponseEntity<MessageDTO> createNewGoal(@RequestBody Goal goal,
                                           @RequestHeader String authorization)
      throws PfpException {
    return ResponseEntity.ok().body(goalService.createNewGoal(goal, authorization));
  }

  @PostMapping("/deposit")
  ResponseEntity<MessageDTO> depositIntoGoal(@RequestBody Goal goal,
                                             @RequestHeader String authorization)
      throws PfpException {
    return ResponseEntity.ok().body(goalService.deposit(goal, goal.getDeposit(), authorization));
  }

  @PostMapping("/withdrawal")
  ResponseEntity<MessageDTO> withdrawalFromGoal(@RequestBody Goal goal,
                                                @RequestHeader String authorization)
      throws PfpException {
    return ResponseEntity.ok()
        .body(goalService.withdrawal(goal, goal.getWithdrawal(), authorization));
  }

  @GetMapping("/progress")
  ResponseEntity<GoalProgressResponseDTO> progressOfGoal(@RequestBody Goal goal,
                                                         @RequestHeader String authorization)
      throws PfpException {
    return ResponseEntity.ok().body(goalService.checkProgress(goal, authorization));
  }

  @GetMapping("/goals")
  ResponseEntity<List<GoalProgressResponseDTO>> showAllGoals(@RequestHeader String authorization)
      throws PfpException {
    return ResponseEntity.ok().body(goalService.showAllGoal(authorization));
  }

  @DeleteMapping("/delete_goal")
  ResponseEntity<MessageDTO> deleteGoal(@RequestBody Goal goal, @RequestHeader String authorization)
      throws PfpException {
    return ResponseEntity.ok().body(goalService.deleteGoal(goal, authorization));
  }

  @PatchMapping("/edit_goal")
  ResponseEntity<MessageDTO> editGoal(@RequestParam String name, @RequestBody(required = false) Goal goal,
                                      @RequestHeader String authorization) throws PfpException {
    return ResponseEntity.ok().body(goalService.editGoal(name, goal, authorization));
  }
}
