package org.example.javalaborbeadando.felhasznalo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@TestPropertySource(properties = "spring.autoconfigure.exclude=")
class FelhasznaloRepositoryTest {

    @Autowired
    private FelhasznaloRepository repository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void savesAndFindsUserByUsername() {
        Felhasznalo saved = repository.saveAndFlush(new Felhasznalo("teszt", "teszt-hash", "USER"));

        assertThat(saved.getId()).isNotNull().isPositive();
        Felhasznalo found = repository.findByUsername("teszt").orElseThrow();
        assertThat(found.getId()).isEqualTo(saved.getId());
        assertThat(found.getJelszoHash()).isEqualTo("teszt-hash");
        assertThat(found.getSzerepkor()).isEqualTo("USER");
        assertThat(repository.findByUsername("ismeretlen")).isEmpty();
    }

    @Test
    void rejectsDuplicateUsernames() {
        jdbcTemplate.update("INSERT INTO felhasznalo (username, jelszo_hash, szerepkor) VALUES (?, ?, ?)",
                "teszt", "hash", "USER");
        assertThatThrownBy(() -> jdbcTemplate.update(
                "INSERT INTO felhasznalo (username, jelszo_hash, szerepkor) VALUES (?, ?, ?)",
                "teszt", "masik-hash", "USER"))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
