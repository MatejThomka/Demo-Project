package com.mth.demo.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.mth.demo.exception.UnauthorizedException;
import com.mth.demo.exception.financeexceptions.FinanceErrorException;
import com.mth.demo.exception.financeexceptions.FinanceNotFoundException;
import com.mth.demo.models.dto.finance.CategoryDTO;
import com.mth.demo.models.entities.finance.Category;
import com.mth.demo.models.entities.user.RoleType;
import com.mth.demo.models.entities.user.User;
import com.mth.demo.repositories.CategoryRepository;
import com.mth.demo.repositories.UserRepository;
import com.mth.demo.security.config.JwtService;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CategoryServiceImplTest {

    @Test
    void createNewCategory_OK() throws FinanceErrorException {
        //arrange
        var jwtServiceMock = mock(JwtService.class);
        var userRepositoryMock = mock(UserRepository.class);
        var categoryRepositoryMock = mock(CategoryRepository.class);
        var categoryService = new CategoryServiceImpl(jwtServiceMock, categoryRepositoryMock, userRepositoryMock);

        var user = new User(1L, "firstName", "lastName", "mail@mail", "password", LocalDate.now(), RoleType.USER);
        var categoryDtoInput = new CategoryDTO(0L, "clothes");
        var categoryDtoResult = new CategoryDTO(0L, "clothes");

        when(jwtServiceMock.extractUserNameFromHeader(any())).thenReturn(user.getEmail());
        when(userRepositoryMock.findByEmailOrderById(user.getEmail())).thenReturn(user);

        //act
        var result = categoryService.createNewCategory(categoryDtoInput, any());

        //assert
        Assertions.assertEquals(categoryDtoResult, result);
    }

    @Test
    void createNewCategory_NameIsNull() {
        //arrange
        var jwtServiceMock = mock(JwtService.class);
        var userRepositoryMock = mock(UserRepository.class);
        var categoryRepositoryMock = mock(CategoryRepository.class);
        var categoryService = new CategoryServiceImpl(jwtServiceMock, categoryRepositoryMock, userRepositoryMock);

        var user = new User(1L, "firstName", "lastName", "mail@mail", "password", LocalDate.now(), RoleType.USER);
        var categoryDtoInput = new CategoryDTO(0L, null);

        when(jwtServiceMock.extractUserNameFromHeader(any())).thenReturn(user.getEmail());
        when(userRepositoryMock.findByEmailOrderById(user.getEmail())).thenReturn(user);

        //act and assert
        Assertions.assertThrows(FinanceErrorException.class, () -> categoryService.createNewCategory(categoryDtoInput, "request"));
    }

    @Test
    void readAllCategories_notFoundAnything() {
        //arrange
        var jwtServiceMock = mock(JwtService.class);
        var userRepositoryMock = mock(UserRepository.class);
        var categoryRepositoryMock = mock(CategoryRepository.class);
        var categoryService = new CategoryServiceImpl(jwtServiceMock, categoryRepositoryMock, userRepositoryMock);

        var user = new User(1L, "firstName", "lastName", "mail@mail", "password", LocalDate.now(), RoleType.USER);

        when(jwtServiceMock.extractUserNameFromHeader(any())).thenReturn(user.getEmail());
        when(userRepositoryMock.findByEmailOrderById(user.getEmail())).thenReturn(user);

        //act and assert
        Assertions.assertThrows(FinanceNotFoundException.class, () -> categoryService.readAllCategories("request"));
    }

    @Test
    void updateCategory_notExistCategoryId_OK() {
        //arrange
        var jwtServiceMock = mock(JwtService.class);
        var userRepositoryMock = mock(UserRepository.class);
        var categoryRepositoryMock = mock(CategoryRepository.class);
        var categoryService = new CategoryServiceImpl(jwtServiceMock, categoryRepositoryMock, userRepositoryMock);

        var categoryDtoInput = new CategoryDTO(1L, "clothes");

        //act and assert
        Assertions.assertThrows(FinanceNotFoundException.class, () -> categoryService.updateCategory(0L, categoryDtoInput, "request"));
    }

    @Test
    void updateCategory_notAuthorisedUser_OK() {
        //arrange
        var jwtServiceMock = mock(JwtService.class);
        var userRepositoryMock = mock(UserRepository.class);
        var categoryRepositoryMock = mock(CategoryRepository.class);
        var categoryService = new CategoryServiceImpl(jwtServiceMock, categoryRepositoryMock, userRepositoryMock);

        var user = new User(1L, "firstName", "lastName", "mail@mail", "password", LocalDate.now(), RoleType.USER);
        var categoryDtoInput = new CategoryDTO(1L, "clothes");
        var category = new Category(7L, 1L, "clothes");
        long categoryId = 7L;

        when(jwtServiceMock.extractUserNameFromHeader("request")).thenReturn(user.getEmail());
        when(userRepositoryMock.findByEmailOrderById(user.getEmail())).thenReturn(user);
        when(categoryRepositoryMock.findById(categoryId)).thenReturn(Optional.of(category));

        //act and assert
        Assertions.assertThrows(UnauthorizedException.class, () -> categoryService.updateCategory(7L, categoryDtoInput, "request"));
    }

}