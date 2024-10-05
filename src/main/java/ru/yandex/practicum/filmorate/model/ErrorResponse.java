package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ErrorResponse {
    String message;
    String error;

    public ErrorResponse(String message, String error) {
        this.message = message;
        this.error = error;
    }
}
