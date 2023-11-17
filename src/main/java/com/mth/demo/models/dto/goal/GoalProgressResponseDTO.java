package com.mth.demo.models.dto.goal;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GoalProgressResponseDTO {
  @JsonAlias(value = "name of goal")
  String name;
  @JsonAlias(value = "current balance")
  Double currentBalance;
  @JsonAlias(value = "goal target")
  Double targetGoal;
  @JsonAlias(value = "last deposit")
  Double lastDeposit;
  @JsonAlias(value = "last withdrawal")
  Double lastWithdrawal;
  @JsonAlias(value = "percentage progress")
  Integer percentage;
  @JsonAlias(value = "message")
  String message;
}