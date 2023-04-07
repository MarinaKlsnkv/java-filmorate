package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private final ConcurrentHashMap<Long, User> users = new ConcurrentHashMap<>();

    private final AtomicLong index = new AtomicLong(0);

    @Override
    public User addUser(User user) {
        index.addAndGet(1);
        Long id = index.longValue();
        user.setId(id);
        users.put(id, user);
        return users.get(id);
    }

    @Override
    public User updateUser(User user) {
        if (!users.containsKey(user.getId())) {
            log.error("User doesn't exist");
            throw new UserNotFoundException("User doesn't exist");
        }
        users.put(user.getId(), user);
        return users.get(user.getId());
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getById(Long id) {
        User user = users.get(id);
        if (user == null) {
            throw new UserNotFoundException("User with id " + id + " doesn't exist");
        }
        return user;
    }
}
