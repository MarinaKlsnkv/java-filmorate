package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.ValidationResult;
import ru.yandex.practicum.filmorate.services.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private UserStorage userStorage;
    private UserService userService;

    public UserController(@Qualifier("userDbStorage") UserStorage userStorage, UserService userService) {
        this.userStorage = userStorage;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        setLoginValueToNameIfNameIsNullOrBlank(user);
        ValidationResult validationResult = validate(user);
        if (!validationResult.isValid()) {
            log.error("Not valid user: {}. Errors: {}", user, validationResult.getMessages());
            throw new ValidationException(String.join(", ", validationResult.getMessages()));
        }
        User savedUser = userStorage.addUser(user);
        log.info("Added a user {}", savedUser);
        return ResponseEntity.ok(savedUser);
    }

    @PutMapping
    public ResponseEntity<User> updateUser(@RequestBody User user) {
        setLoginValueToNameIfNameIsNullOrBlank(user);

        ValidationResult validationResult = validate(user);
        if (!validationResult.isValid()) {
            throw new ValidationException(String.join(", ", validationResult.getMessages()));
        }
        User updatedUser = userStorage.updateUser(user);
        log.info("Updated a user {}", updatedUser);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping
    public ResponseEntity<List<User>> getUsers() {
        List<User> allUsers = userStorage.getAllUsers();
        return ResponseEntity.ok(allUsers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userStorage.getById(id);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public ResponseEntity<String> addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        userService.addFriend(id, friendId);
        return ResponseEntity.ok("Friend was added");
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public ResponseEntity<String> deleteFriend(@PathVariable Long id, @PathVariable Long friendId) {
        userService.deleteFriend(id, friendId);
        return ResponseEntity.ok("Friend was deleted");
    }

    @GetMapping("{id}/friends")
    public ResponseEntity<List<User>> getFriends(@PathVariable Long id) {
        List<User> friends = userService.getFriends(id);
        return ResponseEntity.ok(friends);
    }

    @GetMapping("{id}/friends/common/{otherId}")
    public ResponseEntity<List<User>> getCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        List<User> commonFriends = userService.getCommonFriends(id, otherId);
        return ResponseEntity.ok(commonFriends);
    }

    private void setLoginValueToNameIfNameIsNullOrBlank(User user) {
        String name = user.getName();
        if (name == null || name.isBlank()) {
            user.setName(user.getLogin());
        }
    }

    private ValidationResult validate(User user) {
        ValidationResult validationResult = new ValidationResult(true, new ArrayList<>());
        List<String> messages = validationResult.getMessages();
        if (user.getEmail().isBlank()) {
            validationResult.setValid(false);
            messages.add("Email should not be blank");
        }
        if (!user.getEmail().contains("@")) {
            validationResult.setValid(false);
            messages.add("Email should contain '@' symbol");
        }
        if (user.getLogin().isBlank()) {
            validationResult.setValid(false);
            messages.add("Login should not be blank");
        }
        if (user.getLogin().contains(" ")) {
            validationResult.setValid(false);
            messages.add("Login should not have spaces");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            validationResult.setValid(false);
            messages.add("Birthday cannot be in the future");
        }

        return validationResult;
    }

}
