package org.example.javalaborbeadando.megye;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = "spring.autoconfigure.exclude=")
class MegyeRepositoryTest {

    @Autowired
    private MegyeRepository repository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void readsCountyFromDatabase() {
        jdbcTemplate.update("INSERT INTO megye (id, nev, regio) VALUES (?, ?, ?)",
                15, "Győr-Moson-Sopron", "Nyugat-Dunántúl");

        Megye megye = repository.findById(15).orElseThrow();

        assertThat(megye.getId()).isEqualTo(15);
        assertThat(megye.getNev()).isEqualTo("Győr-Moson-Sopron");
        assertThat(megye.getRegio()).isEqualTo("Nyugat-Dunántúl");
        assertThat(repository.findById(999)).isEmpty();
    }
}
