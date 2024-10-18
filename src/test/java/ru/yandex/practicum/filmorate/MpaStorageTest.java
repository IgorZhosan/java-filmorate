package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ImportResource;

import java.util.Collection;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ImportResource
@AutoConfigureTestDatabase
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MpaStorageTest {
    private final MpaStorage mpaStorage;

    @Autowired
    public MpaStorageTest(@Qualifier("jdbcMpaStorage") MpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    @Test
    void shouldGetAllMpaTest() {
        Optional<Collection<Mpa>> ratingMPAOptional = Optional.ofNullable(mpaStorage.getAllMpa());
        assertThat(ratingMPAOptional)
                .isPresent()
                .hasValueSatisfying(mpa -> {
                    assertThat(mpa).isNotEmpty();
                    assertThat(mpa).hasSize(5);
                    assertThat(mpa).element(0).hasFieldOrPropertyWithValue("id", 1);
                    assertThat(mpa).element(0).hasFieldOrPropertyWithValue("name", "G");
                    assertThat(mpa).element(1).hasFieldOrPropertyWithValue("id", 2);
                    assertThat(mpa).element(1).hasFieldOrPropertyWithValue("name", "PG");
                    assertThat(mpa).element(2).hasFieldOrPropertyWithValue("id", 3);
                    assertThat(mpa).element(2).hasFieldOrPropertyWithValue("name", "PG-13");
                    assertThat(mpa).element(3).hasFieldOrPropertyWithValue("id", 4);
                    assertThat(mpa).element(3).hasFieldOrPropertyWithValue("name", "R");
                    assertThat(mpa).element(4).hasFieldOrPropertyWithValue("id", 5);
                    assertThat(mpa).element(4).hasFieldOrPropertyWithValue("name", "NC-17");
                });
    }

    @Test
    void shouldGetMpaByIdTest() {
        Optional<Mpa> ratingMPAOptional = mpaStorage.getMpaById(1);
        assertThat(ratingMPAOptional)
                .isPresent()
                .hasValueSatisfying(mpa -> {
                            assertThat(mpa).hasFieldOrPropertyWithValue("id", 1);
                            assertThat(mpa).hasFieldOrPropertyWithValue("name", "G");
                        }
                );
    }
}
