package ru.yandex.practicum.filmorate.exception;

public class InvalidFilmException extends RuntimeException {
    public InvalidFilmException(String message) {
        super(message);
    }
}
