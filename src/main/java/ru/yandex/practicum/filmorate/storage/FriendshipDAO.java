package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.utils.FriendshipUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@Slf4j
public class FriendshipDAO {

    private final JdbcTemplate jdbcTemplate;

    public FriendshipDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Friendship> findFriendsByUserId(Long userId) {
        String sql = "SELECT * FROM friendship WHERE user_id = ?";
        return jdbcTemplate.query(sql, new Object[]{userId}, new FriendshipRowMapper());
    }

    public Friendship findByUserAndFriendIds(Long userId, Long friendId) {
        String sql = "SELECT * FROM friendship WHERE user_id = ? AND friend_id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{userId, friendId}, new FriendshipRowMapper());
    }

    public void updateConfirmedStatusByUserAndFriendIds(Long userId, Long friendId, boolean confirmed) {
        String sql = "UPDATE friendship SET confirmed = ? WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sql, confirmed, userId, friendId);
    }

    private static class FriendshipRowMapper implements RowMapper<Friendship> {
        @Override
        public Friendship mapRow(ResultSet rs, int rowNum) throws SQLException {
            Friendship friendship = new Friendship();
            friendship.setUserId(rs.getLong("user_id"));
            friendship.setFriendId(rs.getLong("friend_id"));
            friendship.setConfirmed(rs.getBoolean("confirmed"));
            return friendship;
        }
    }

    private Friendship mapRowToFriendship(ResultSet resultSet, int rowNum) throws SQLException {
        return new Friendship(
                resultSet.getLong("user_id"),
                resultSet.getLong("friend_id"),
                resultSet.getBoolean("confirmed")
        );
    }

    public void addFriendship(Friendship friendship) {
        simpleSave(friendship);
    }

    public void deleteFriendship(Long userId, Long friendId) {
        String sql = "DELETE FROM friendship WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sql, userId, friendId);
    }

    private void simpleSave(Friendship friendship) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("friendship")
                .usingGeneratedKeyColumns("id");
        simpleJdbcInsert.execute(FriendshipUtils.friendshipToMap(friendship));
    }

}
