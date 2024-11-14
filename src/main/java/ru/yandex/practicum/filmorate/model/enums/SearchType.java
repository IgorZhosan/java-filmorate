package ru.yandex.practicum.filmorate.model.enums;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum SearchType {
    DIRECTOR("director"),
    TITLE("title");

    private final String value;

    SearchType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Set<SearchType> toEnum(String by) {
        return Arrays.stream(by.split(","))
                .map(String::trim)
                .map(String::toUpperCase)
                .map(SearchType::valueOf)
                .collect(Collectors.toSet());
    }
}