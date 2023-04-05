package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    private static final User PREDEFINED_USER =
            new User("example@gmail.com", "login_string",
                    "name_string", LocalDate.of(1990, 1, 1));
    private static final String NOT_VALID_EMAIL_VALIDATION_ERR = "Email should not be blank";
    private static final String EMAIL_DOESNT_HAVE_AT_SYMBOL_VALIDATION_ERR = "Email should contain '@' symbol";
    private static final String BLANK_LOGIN_VALIDATION_ERR = "Login should not be blank";
    private static final String LOGIN_CONTAINS_SPACES_VALIDATION_ERR = "Login should not have spaces";
    private static final String BIRTHDAY_IN_tHE_FUTURE_VALIDATION_ERR = "Birthday cannot be in the future";

    private UserController userController;

    @BeforeEach
    void init() {
        userController = new UserController();
        userController.createUser(PREDEFINED_USER);
    }

    //*****Create User Tests******//

    @Test
    void createUserShouldThrowWhenEmailIsEmpty() {
        User user = new User("", "myLogin", "myName", LocalDate.of(1990, 1, 1));
        Exception exception = assertThrows(ValidationException.class, () -> {
            userController.createUser(user);
        });
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(NOT_VALID_EMAIL_VALIDATION_ERR));
    }

    @Test
    void createUserShouldThrowWhenEmailDoesntContainAtSymbol() {
        User user = new User("emailwithoutat.com", "myLogin", "myName", LocalDate.of(1990, 1, 1));
        Exception exception = assertThrows(ValidationException.class, () -> {
            userController.createUser(user);
        });
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(EMAIL_DOESNT_HAVE_AT_SYMBOL_VALIDATION_ERR));
    }

    @Test
    void createUserShouldThrowWhenLoginIsBlank() {
        User user = new User("email@gmail.com", "", "myName", LocalDate.of(1990, 1, 1));
        Exception exception = assertThrows(ValidationException.class, () -> {
            userController.createUser(user);
        });
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(BLANK_LOGIN_VALIDATION_ERR));
    }

    @Test
    void createUserShouldThrowWhenLoginHasSpaces() {
        User user = new User("email@gmail.com", "dfdf dfdf", "myName", LocalDate.of(1990, 1, 1));
        Exception exception = assertThrows(ValidationException.class, () -> {
            userController.createUser(user);
        });
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(LOGIN_CONTAINS_SPACES_VALIDATION_ERR));
    }

    @Test
    void createUserShouldThrowWhenBirthdayIsInTheFuture() {
        User user = new User("email@gmail.com", "login", "myName", LocalDate.of(2025, 1, 1));
        Exception exception = assertThrows(ValidationException.class, () -> {
            userController.createUser(user);
        });
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(BIRTHDAY_IN_tHE_FUTURE_VALIDATION_ERR));
    }

    @Test
    void createUserNameEqualsLoginWhenNameWasBlank() {
        User user = new User("email@gmail.com", "login", "", LocalDate.of(2020, 1, 1));
        Long id = userController.createUser(user).getBody().getId();
        boolean nameEqualsLogin = userController.getUsers().toString().contains("name=login");
        assertTrue(nameEqualsLogin);
    }

    @Test
    void createUserWorksWithoutErrors() {
        User user = new User("email@gmail.com", "login", "Masha", LocalDate.of(2020, 1, 1));
        assertDoesNotThrow(() -> {
            userController.createUser(user);
        });
    }

    //*****Update User Tests******//

    @Test
    void updateUserShouldThrowWhenEmailIsEmpty() {
        User user = new User("", "myLogin", "myName", LocalDate.of(1990, 1, 1));
        user.setId(1L);
        Exception exception = assertThrows(ValidationException.class, () -> {
            userController.updateUser(user);
        });
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(NOT_VALID_EMAIL_VALIDATION_ERR));
    }

    @Test
    void updateUserShouldThrowWhenEmailDoesntContainAtSymbol() {
        User user = new User("emailwithoutat.com", "myLogin", "myName", LocalDate.of(1990, 1, 1));
        user.setId(1L);
        Exception exception = assertThrows(ValidationException.class, () -> {
            userController.updateUser(user);
        });
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(EMAIL_DOESNT_HAVE_AT_SYMBOL_VALIDATION_ERR));
    }

    @Test
    void updateUserShouldThrowWhenLoginIsBlank() {
        User user = new User("email@gmail.com", "", "myName", LocalDate.of(1990, 1, 1));
        user.setId(1L);
        Exception exception = assertThrows(ValidationException.class, () -> {
            userController.updateUser(user);
        });
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(BLANK_LOGIN_VALIDATION_ERR));
    }

    @Test
    void updateUserShouldThrowWhenLoginHasSpaces() {
        User user = new User("email@gmail.com", "dfdf dfdf", "myName", LocalDate.of(1990, 1, 1));
        user.setId(1L);
        Exception exception = assertThrows(ValidationException.class, () -> {
            userController.updateUser(user);
        });
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(LOGIN_CONTAINS_SPACES_VALIDATION_ERR));
    }

    @Test
    void updateUserShouldThrowWhenBirthdayIsInTheFuture() {
        User user = new User("email@gmail.com", "login", "myName", LocalDate.of(2025, 1, 1));
        user.setId(1L);
        Exception exception = assertThrows(ValidationException.class, () -> {
            userController.updateUser(user);
        });
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(BIRTHDAY_IN_tHE_FUTURE_VALIDATION_ERR));
    }

    @Test
    void updateUserNameEqualsLoginWhenNameWasBlank() {
        User user = new User("email@gmail.com", "login", "", LocalDate.of(2020, 1, 1));
        user.setId(1L);
        Long id = userController.updateUser(user).getBody().getId();
        boolean nameEqualsLogin = userController.getUsers().toString().contains("name=login");
        assertTrue(nameEqualsLogin);
    }

    @Test
    void updateUserWorksWithoutErrors() {
        String login = "login!!!";
        User user = new User("email@gmail.com", login, "Vasya", LocalDate.of(2020, 1, 1));
        user.setId(1L);
        userController.updateUser(user);

        String actualLogin = userController.getUsers().getBody().get(0).getLogin();
        assertEquals(login, actualLogin);
    }

    //*****Get Users Tests******//

    @Test
    void getUsersWorksWithoutErrors() {
        List<User> expected = new ArrayList<>(List.of(PREDEFINED_USER));

        List<User> actual = userController.getUsers().getBody();

        assertEquals(expected, actual);
    }
}