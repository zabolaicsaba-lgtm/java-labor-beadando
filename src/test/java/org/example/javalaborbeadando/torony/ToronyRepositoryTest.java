package org.example.javalaborbeadando.torony;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = "spring.autoconfigure.exclude=")
class ToronyRepositoryTest {

    @Autowired
    private ToronyRepository repository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void readsTowerAndItsLocationAndCounty() {
        jdbcTemplate.update("INSERT INTO megye (id, nev, regio) VALUES (?, ?, ?)",
                15, "Győr-Moson-Sopron", "Nyugat-Dunántúl");
        jdbcTemplate.update("INSERT INTO helyszin (id, nev, megyeid) VALUES (?, ?, ?)",
                4, "Mosonmagyaróvár", 15);
        jdbcTemplate.update("INSERT INTO torony (id, darab, teljesitmeny, kezdev, helyszinid) VALUES (?, ?, ?, ?, ?)",
                10, 5, 2000, 2005, 4);

        Torony torony = repository.findById(10).orElseThrow();

        assertThat(torony.getId()).isEqualTo(10);
        assertThat(torony.getDarab()).isEqualTo(5);
        assertThat(torony.getTeljesitmeny()).isEqualTo(2000);
        assertThat(torony.getKezdev()).isEqualTo(2005);
        assertThat(torony.getHelyszin().getId()).isEqualTo(4);
        assertThat(torony.getHelyszin().getNev()).isEqualTo("Mosonmagyaróvár");
        assertThat(torony.getHelyszin().getMegye().getNev()).isEqualTo("Győr-Moson-Sopron");
        assertThat(repository.findById(999)).isEmpty();
    }
}
