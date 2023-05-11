package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.List;

@RestController
@RequestMapping("/mpa")
@Slf4j
public class MpaController {

    private final MpaStorage mpaStorage;

    public MpaController(MpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    @GetMapping
    public ResponseEntity<List<MpaRating>> getMpaRatings() {
        List<MpaRating> allMpaRatings = mpaStorage.getAll();
        return ResponseEntity.ok(allMpaRatings);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MpaRating> getMpaById(@PathVariable Long id) {
        MpaRating mpaRating = mpaStorage.getById(id);
        return ResponseEntity.ok(mpaRating);
    }

}
