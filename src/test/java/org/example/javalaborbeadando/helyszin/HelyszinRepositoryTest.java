package org.example.javalaborbeadando.helyszin;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = "spring.autoconfigure.exclude=")
class HelyszinRepositoryTest {

    @Autowired
    private HelyszinRepository repository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void readsLocationAndItsCounty() {
        jdbcTemplate.update("INSERT INTO megye (id, nev, regio) VALUES (?, ?, ?)",
                15, "Győr-Moson-Sopron", "Nyugat-Dunántúl");
        jdbcTemplate.update("INSERT INTO helyszin (id, nev, megyeid) VALUES (?, ?, ?)",
                4, "Mosonmagyaróvár", 15);

        Helyszin helyszin = repository.findById(4).orElseThrow();

        assertThat(helyszin.getId()).isEqualTo(4);
        assertThat(helyszin.getNev()).isEqualTo("Mosonmagyaróvár");
        assertThat(helyszin.getMegye().getId()).isEqualTo(15);
        assertThat(helyszin.getMegye().getNev()).isEqualTo("Győr-Moson-Sopron");
        assertThat(helyszin.getMegye().getRegio()).isEqualTo("Nyugat-Dunántúl");
        assertThat(repository.findById(999)).isEmpty();
    }
}
