package com.mth.demo.models.entities.finance;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mth.demo.models.entities.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "finance_goal")
public class Goal {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @JsonIgnore
  Long id;
  String name;
  Double targetGoal;
  Double deposit;
  Double withdrawal;
  Double currentBalance;
  @ManyToOne
  @JsonIgnore
  User user;

  public Goal(String name, Double targetGoal, User user) {
    this.name = name;
    this.targetGoal = targetGoal;
    this.currentBalance = 0.00;
    this.deposit = 0.00;
    this.withdrawal = 0.00;
    setUser(user);
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }
}
