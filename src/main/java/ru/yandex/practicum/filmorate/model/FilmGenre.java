package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Table;

@Table("film_genre")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilmGenre {

    private Long filmId;
    private Long genreId;

}
