package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Table;

@Table("film_like")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilmLike {

    private Long userId;
    private Long filmId;

}
