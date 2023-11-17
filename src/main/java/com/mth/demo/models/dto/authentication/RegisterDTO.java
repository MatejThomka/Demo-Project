package com.mth.demo.models.dto.authentication;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class RegisterDTO implements Serializable {

  String firstname;
  String lastname;
  String email;
  String password;

}