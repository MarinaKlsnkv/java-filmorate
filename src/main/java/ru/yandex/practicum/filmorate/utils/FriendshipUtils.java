package ru.yandex.practicum.filmorate.utils;

import ru.yandex.practicum.filmorate.model.Friendship;

import java.util.HashMap;
import java.util.Map;

public class FriendshipUtils {

    public static Map<String, Object> friendshipToMap(Friendship friendship) {
        Map<String, Object> values = new HashMap<>();
        values.put("user_id", friendship.getUserId());
        values.put("friend_id", friendship.getFriendId());
        values.put("confirmed", friendship.getConfirmed());
        return values;
    }
}
