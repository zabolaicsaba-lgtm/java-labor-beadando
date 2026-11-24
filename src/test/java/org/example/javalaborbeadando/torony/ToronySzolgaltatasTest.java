package org.example.javalaborbeadando.torony;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:toronycrud;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.open-in-view=false"
})
@ActiveProfiles("mysql")
class ToronySzolgaltatasTest {

    @Autowired
    private ToronySzolgaltatas szolgaltatas;

    @Autowired
    private JdbcTemplate jdbc;

    @BeforeEach
    void elokeszites() {
        jdbc.update("DELETE FROM torony");
        jdbc.update("DELETE FROM helyszin");
        jdbc.update("DELETE FROM megye");
        jdbc.update("INSERT INTO megye (id, nev, regio) VALUES (15, 'Győr-Moson-Sopron', 'Nyugat-Dunántúl')");
        jdbc.update("INSERT INTO helyszin (id, nev, megyeid) VALUES (4, 'Mosonmagyaróvár', 15)");
        jdbc.update("INSERT INTO helyszin (id, nev, megyeid) VALUES (17, 'Levél', 15)");
    }

    @Test
    void createUpdateAndDelete() {
        Integer id = szolgaltatas.letrehozas(new ToronyAdatok(2, 2000, 2010, 4));
        assertThat(id).isNotNull().isPositive();
        assertThat(jdbc.queryForObject("SELECT darab FROM torony WHERE id = ?", Integer.class, id)).isEqualTo(2);

        szolgaltatas.modositas(id, new ToronyAdatok(5, 3000, 2011, 17));
        assertThat(jdbc.queryForObject("SELECT darab FROM torony WHERE id = ?", Integer.class, id)).isEqualTo(5);
        assertThat(jdbc.queryForObject("SELECT teljesitmeny FROM torony WHERE id = ?", Integer.class, id)).isEqualTo(3000);
        assertThat(jdbc.queryForObject("SELECT kezdev FROM torony WHERE id = ?", Integer.class, id)).isEqualTo(2011);
        assertThat(jdbc.queryForObject("SELECT helyszinid FROM torony WHERE id = ?", Integer.class, id)).isEqualTo(17);

        szolgaltatas.torles(id);
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM torony WHERE id = ?", Integer.class, id)).isZero();
    }

    @Test
    void rejectsInvalidInputWithoutPersisting() {
        assertThatThrownBy(() -> szolgaltatas.letrehozas(new ToronyAdatok(0, -1, 0, null)))
                .isInstanceOf(ConstraintViolationException.class);
        assertThatThrownBy(() -> szolgaltatas.letrehozas(null))
                .isInstanceOf(ConstraintViolationException.class);
        assertThatThrownBy(() -> szolgaltatas.letrehozas(new ToronyAdatok(1, 100, 10000, 4)))
                .isInstanceOf(ConstraintViolationException.class);
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM torony", Integer.class)).isZero();
    }

    @Test
    void missingRecordsFailWithoutChangingExistingTower() {
        Integer id = szolgaltatas.letrehozas(new ToronyAdatok(2, 2000, 2010, 4));

        assertThatThrownBy(() -> szolgaltatas.letrehozas(new ToronyAdatok(1, 100, 2010, 999)))
                .isInstanceOf(NoSuchElementException.class);
        assertThatThrownBy(() -> szolgaltatas.modositas(id, new ToronyAdatok(3, 3000, 2011, 999)))
                .isInstanceOf(NoSuchElementException.class);
        assertThatThrownBy(() -> szolgaltatas.modositas(999, new ToronyAdatok(3, 3000, 2011, 4)))
                .isInstanceOf(NoSuchElementException.class);
        assertThatThrownBy(() -> szolgaltatas.torles(999))
                .isInstanceOf(NoSuchElementException.class);

        assertThat(jdbc.queryForObject("SELECT darab FROM torony WHERE id = ?", Integer.class, id)).isEqualTo(2);
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM torony", Integer.class)).isEqualTo(1);
    }
}
