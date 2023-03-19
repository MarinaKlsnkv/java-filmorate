package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.ValidationResult;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final ConcurrentHashMap<Long, User> users = new ConcurrentHashMap<>();
    private final AtomicLong index = new AtomicLong(0);

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        setLoginValueToNameIfNameIsNullOrBlank(user);
        ValidationResult validationResult = validate(user);
        if (!validationResult.isValid()) {
            log.error("Not valid user: {}. Errors: {}", user, validationResult.getMessages());
            throw new ValidationException(String.join(", ", validationResult.getMessages()));
        }
        index.addAndGet(1);
        Long id = index.longValue();
        user.setId(id);
        users.put(id, user);
        User savedUser = users.get(id);
        log.info("Added a user {}", savedUser);
        return ResponseEntity.ok(savedUser);
    }

    @PutMapping
    public ResponseEntity<User> updateUser(@RequestBody User user) {
        setLoginValueToNameIfNameIsNullOrBlank(user);
        if (!users.containsKey(user.getId())) {
            throw new ValidationException("User doesn't exist");
        };
        ValidationResult validationResult = validate(user);
        if (!validationResult.isValid()) {
            throw new ValidationException(String.join(", ", validationResult.getMessages()));
        }
        users.put(user.getId(), user);
        User updatedUser = users.get(user.getId());
        log.info("Updated a user {}", updatedUser);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping
    public ResponseEntity<List<User>> getUsers() {
        List<User> allUsers = new ArrayList<>();
        allUsers.addAll(users.values());
        return ResponseEntity.ok(allUsers);
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
