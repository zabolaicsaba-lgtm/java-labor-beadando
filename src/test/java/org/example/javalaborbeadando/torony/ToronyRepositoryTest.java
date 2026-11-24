package org.example.javalaborbeadando.torony;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.example.javalaborbeadando.helyszin.HelyszinRepository;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = "spring.autoconfigure.exclude=")
class ToronyRepositoryTest {

    @Autowired
    private ToronyRepository repository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private HelyszinRepository helyszinRepository;

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

    @Test
    void assignsIdWhenSavingNewTower() {
        jdbcTemplate.update("INSERT INTO megye (id, nev, regio) VALUES (?, ?, ?)",
                15, "Győr-Moson-Sopron", "Nyugat-Dunántúl");
        jdbcTemplate.update("INSERT INTO helyszin (id, nev, megyeid) VALUES (?, ?, ?)",
                4, "Mosonmagyaróvár", 15);

        Torony torony = repository.saveAndFlush(new Torony(2, 2000, 2010,
                helyszinRepository.findById(4).orElseThrow()));

        assertThat(torony.getId()).isNotNull().isPositive();
        assertThat(jdbcTemplate.queryForObject("SELECT darab FROM torony WHERE id = ?",
                Integer.class, torony.getId())).isEqualTo(2);
        assertThat(jdbcTemplate.queryForObject("SELECT helyszinid FROM torony WHERE id = ?",
                Integer.class, torony.getId())).isEqualTo(4);
    }
}
