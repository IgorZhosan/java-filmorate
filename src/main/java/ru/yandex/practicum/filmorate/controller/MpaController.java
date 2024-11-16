package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.mpa.MpaServiceImpl;

import java.util.List;

@RestController
@RequestMapping("/mpa")
@Slf4j
@RequiredArgsConstructor
@Validated
public class MpaController {

    private final MpaServiceImpl mpaService;

    @GetMapping
    public List<Mpa> getRatings() {
        return mpaService.getMpa();
    }

    @GetMapping("/{id}")
    public Mpa getRating(@PathVariable @Positive Integer id) {
        return mpaService.getRatingById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mpa addRating(@RequestBody Mpa rating) {
        return mpaService.addRating(rating);
    }
}