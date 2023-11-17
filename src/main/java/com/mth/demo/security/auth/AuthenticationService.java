package com.mth.demo.security.auth;

import com.mth.demo.exception.EmailIsMissingException;
import com.mth.demo.exception.ExceptionMessage;
import com.mth.demo.exception.PasswordIsMissingException;
import com.mth.demo.exception.PfpException;
import com.mth.demo.exception.UserAlreadyExistsException;
import com.mth.demo.models.dto.authentication.AuthenticationResponseDTO;
import com.mth.demo.models.dto.authentication.LoginDTO;
import com.mth.demo.models.dto.authentication.RegisterDTO;
import com.mth.demo.models.entities.user.RoleType;
import com.mth.demo.models.entities.user.User;
import com.mth.demo.repositories.UserRepository;
import com.mth.demo.security.config.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

  private final UserRepository userRepository;

  private final PasswordEncoder passwordEncoder;

  private final JwtService jwtService;

  private final AuthenticationManager authenticationManager;

  public AuthenticationResponseDTO createUser(RegisterDTO request) throws PfpException {
    if (userRepository.findByEmail(request.getEmail()).isPresent()) {
      throw new UserAlreadyExistsException(ExceptionMessage.USER_ALREADY_EXIST.getMessage());
    }
    var user = User.builder().firstName(request.getFirstname()).lastName(request.getLastname())
        .email(request.getEmail()).password(passwordEncoder.encode(request.getPassword()))
        .join(LocalDate.now()).role(RoleType.USER).build();
    userRepository.save(user);
    var jwtToken = jwtService.generateToken(user);
    return AuthenticationResponseDTO.builder().token(jwtToken).build();
  }

  public AuthenticationResponseDTO authenticate(LoginDTO request) throws PfpException {

    if (request.getEmail().isEmpty()) {
      throw new EmailIsMissingException();
    } else if (request.getPassword().isEmpty()) {
      throw new PasswordIsMissingException();
    } else if (!authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()))
        .isAuthenticated()) {
      throw new BadCredentialsException("Email or Password is incorrect!");
    }
    var user = userRepository.findByEmail(request.getEmail()).orElseThrow();
    var jwtToken = jwtService.generateToken(user);
    return AuthenticationResponseDTO.builder().token(jwtToken).build();
  }

  public AuthenticationResponseDTO logout(String request) {
    jwtService.blacklist(request);
    return AuthenticationResponseDTO.builder().token("You are logged out!").build();
  }

}