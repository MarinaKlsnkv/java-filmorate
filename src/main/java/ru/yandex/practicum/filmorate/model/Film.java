package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class Film {

    @Id
    private Long id;

    private String name;

    private String description;

    private LocalDate releaseDate;

    private Long duration;

    private Long rate;

    private List<Genre> genres = new ArrayList<>();

    private MpaRating mpa;

    public Film() {
        rate = 0L;
    }

    public Film(String name, String description, LocalDate releaseDate, Long duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        rate = 0L;
    }

    public Film(Long id, String name, String description, LocalDate releaseDate, Long duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.rate = 0L;
    }

    public Film(Long id, String name, String description, LocalDate releaseDate, Long duration, Long rate, MpaRating mpaRating) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.rate = rate;
        this.mpa = mpaRating;
    }
}
