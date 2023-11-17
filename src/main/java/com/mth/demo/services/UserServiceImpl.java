package com.mth.demo.services;

import com.mth.demo.exception.ExceptionMessage;
import com.mth.demo.exception.PfpException;
import com.mth.demo.exception.UnauthorizedException;
import com.mth.demo.exception.UserNotFoundException;
import com.mth.demo.models.dto.admin.AdminGetIdDTO;
import com.mth.demo.models.dto.admin.GetEmailDTO;
import com.mth.demo.models.entities.user.RoleType;
import com.mth.demo.models.entities.user.User;
import com.mth.demo.models.entities.user.UserResponseDTO;
import com.mth.demo.repositories.UserRepository;
import com.mth.demo.security.config.JwtService;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final JwtService jwtService;

  @Override
  public User findByEmail(String email) {
    return userRepository.findByEmail(email).orElse(null);
  }

  @Override
  public User findById(Long id) {
    return userRepository.findById(id).orElse(null);
  }

  @Override
  public User getByEmailOrderById(String email) {
    return userRepository.findByEmailOrderById(email);
  }

  @Override
  public AdminGetIdDTO findUserIdByEmail(GetEmailDTO emailDto) {
    var email = getByEmailOrderById(emailDto.getEmail()).getId();
    return AdminGetIdDTO.builder().id(email.toString()).build();
  }

  @Override
  public User updateUser(User user) {
    return userRepository.save(user);
  }

  @Override
  public void deleteUserByEmail(String email, String request) throws PfpException {
    final String jwt = request.substring(7);
    final String userEmail = jwtService.extractUsername(jwt);
    Optional<User> user = userRepository.findByEmail(email);
    if (!user.isPresent()) {
      throw new UserNotFoundException(ExceptionMessage.USER_NOT_FOUND.getMessage());
    } else if (!Objects.equals(email, userEmail)) {
      throw new UserNotFoundException(ExceptionMessage.UNAUTHORIZED.getMessage());
    }
    userRepository.delete(user.get());
  }

  @Override
  public List<UserResponseDTO> findAllUsers(String request) throws PfpException {

    final String jwt = request.substring(7);
    final String userEmail = jwtService.extractUsername(jwt);
    User user = userRepository.findByEmailOrderById(userEmail);

    List<UserResponseDTO> users = new ArrayList<>();

    if (user == null) {
      throw new UserNotFoundException(ExceptionMessage.USER_NOT_FOUND.getMessage());
    } else if (user.getRole() == RoleType.ADMIN) {
      for (int i = 0; i < userRepository.findAll().size(); i++) {
        users.add(
            UserResponseDTO.builder().firstName(userRepository.findAll().get(i).getFirstName())
                .lastName(userRepository.findAll().get(i).getLastName())
                .email(userRepository.findAll().get(i).getEmail()).build());
      }
    } else {
      throw new UnauthorizedException(ExceptionMessage.UNAUTHORIZED.getMessage());
    }

    return users;
  }

  @Override
  public boolean isAdmin(Long userId) throws PfpException {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException(ExceptionMessage.USER_NOT_FOUND.getMessage()));
    return user.getRole().equals(RoleType.ADMIN);
  }

  @Override
  public void deleteUserByIdByAdmin(Long id, Long adminId) throws PfpException {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException(ExceptionMessage.USER_NOT_FOUND.getMessage()));
    if (user.getId().equals(adminId) || isAdmin(adminId)) {
      userRepository.delete(user);
    } else {
      throw new UnauthorizedException(ExceptionMessage.UNAUTHORIZED.getMessage());
    }
  }

  @Override
  public void deleteUserById(Long userId) throws PfpException {
    Optional<User> userOptional = userRepository.findById(userId);
    if (!userOptional.isPresent()) {
      throw new UserNotFoundException(ExceptionMessage.USER_NOT_FOUND.getMessage());
    }
    userRepository.delete(userOptional.get());
  }

}