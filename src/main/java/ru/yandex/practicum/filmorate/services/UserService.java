package ru.yandex.practicum.filmorate.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class UserService {

    private UserStorage userStorage;

    public void addFriend(Long id, Long friendId) {
        User user = userStorage.getById(id);
        User friend = userStorage.getById(friendId);
        if (user == null || friend == null) {
            throw new UserNotFoundException("User not found");
        }
        user.getFriends().add(friendId);
        friend.getFriends().add(id);
    }

    public void deleteFriend(Long id, Long friendId) {
        User user = userStorage.getById(id);
        User friend = userStorage.getById(friendId);
        if (user == null || friend == null) {
            throw new UserNotFoundException("User not found");
        }
        user.getFriends().remove(friendId);
        friend.getFriends().remove(id);
    }

    public List<User> getFriends(Long id) {
        User user = userStorage.getById(id);
        if (user == null) {
            throw new UserNotFoundException("User with id = " + id + " not found");
        }
        return user.getFriends().stream()
                .map((friendId) -> userStorage.getById(friendId))
                .collect(Collectors.toList());
    }


    public List<User> getCommonFriends(Long id, Long anotherId) {
        User user = userStorage.getById(id);
        User anotherUser = userStorage.getById(anotherId);
        if (user == null) {
            throw new UserNotFoundException("User with id = " + id + " not found");
        }
        if (anotherUser == null) {
            throw new UserNotFoundException("User with id = " + anotherId + " not found");
        }
        List<User> userFriends = user.getFriends().stream()
                .map((friendId) -> userStorage.getById(friendId))
                .collect(Collectors.toList());

        List<User> anotherUserFriends = anotherUser.getFriends().stream()
                .map((friendId) -> userStorage.getById(friendId))
                .collect(Collectors.toList());

        userFriends.retainAll(anotherUserFriends);
        return userFriends;
    }
}
