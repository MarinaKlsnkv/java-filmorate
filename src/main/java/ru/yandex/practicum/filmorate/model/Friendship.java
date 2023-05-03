package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Table;

@Table("friendship")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Friendship {
    private Long userId;
    private Long friendId;
    private Boolean confirmed;
}
