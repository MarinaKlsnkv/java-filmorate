package ru.yandex.practicum.filmorate.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.AlreadyFriendsException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendshipDAO;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {

    private final UserStorage userStorage;
    private final FriendshipDAO friendshipDAO;

    public UserService(@Qualifier("userDbStorage") UserStorage userStorage,
                       FriendshipDAO friendshipDAO) {
        this.userStorage = userStorage;
        this.friendshipDAO = friendshipDAO;
    }

    public void addFriend(Long id, Long friendId) {
        User user = userStorage.getById(id);
        User friend = userStorage.getById(friendId);
        if (user == null || friend == null) {
            throw new UserNotFoundException("User not found");
        }
        boolean alreadyFriend = user.getFriends().stream().anyMatch((friendship) ->
                friendship.getFriendId().equals(friendId));
        if (alreadyFriend) {
            throw new AlreadyFriendsException("Already in friend list");
        }

        Friendship friendshipOfFriend = friend.getFriends().stream()
                .filter(fr -> fr.getFriendId().equals(id))
                .findFirst()
                .orElse(null);
        boolean confirmed = false;
        if (friendshipOfFriend != null) {
            confirmed = true;
            friendshipOfFriend.setConfirmed(confirmed);
        }
        friendshipDAO.addFriendship(new Friendship(id, friendId, confirmed));

        user.getFriends().add(new Friendship(id, friendId, confirmed));
    }

    public void deleteFriend(Long id, Long friendId) {
        User user = userStorage.getById(id);
        User friend = userStorage.getById(friendId);
        if (user == null || friend == null) {
            throw new UserNotFoundException("User not found");
        }

        friendshipDAO.deleteFriendship(id, friendId);
        friendshipDAO.updateConfirmedStatusByUserAndFriendIds(friendId, id, false);
    }

    public List<User> getFriends(Long id) {
        User user = userStorage.getById(id);
        if (user == null) {
            throw new UserNotFoundException("User with id = " + id + " not found");
        }

        return friendshipDAO.findFriendsByUserId(id).stream()
                .map((friendship) -> userStorage.getById(friendship.getFriendId()))
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
                .map((friendship) -> userStorage.getById(friendship.getFriendId()))
                .collect(Collectors.toList());

        List<User> anotherUserFriends = anotherUser.getFriends().stream()
                .map((friendship) -> userStorage.getById(friendship.getFriendId()))
                .collect(Collectors.toList());

        userFriends.retainAll(anotherUserFriends);
        return userFriends;
    }

}
