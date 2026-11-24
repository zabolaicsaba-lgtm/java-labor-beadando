package org.example.javalaborbeadando.torony;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:adatbazis;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.open-in-view=false"
})
@ActiveProfiles("mysql")
@AutoConfigureMockMvc
class AdatbazisControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void clearData() {
        jdbcTemplate.update("DELETE FROM torony");
        jdbcTemplate.update("DELETE FROM helyszin");
        jdbcTemplate.update("DELETE FROM megye");
    }

    @Test
    void showsTowerLocationAndCountyForAnonymousVisitors() throws Exception {
        jdbcTemplate.update("INSERT INTO megye (id, nev, regio) VALUES (?, ?, ?)",
                15, "Győr-Moson-Sopron", "Nyugat-Dunántúl");
        jdbcTemplate.update("INSERT INTO helyszin (id, nev, megyeid) VALUES (?, ?, ?)",
                4, "Mosonmagyaróvár", 15);
        jdbcTemplate.update("INSERT INTO torony (id, darab, teljesitmeny, kezdev, helyszinid) VALUES (?, ?, ?, ?, ?)",
                10, 5, 2000, 2005, 4);

        mockMvc.perform(get("/abc123/adatbazis").contextPath("/abc123"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Mosonmagyaróvár")))
                .andExpect(content().string(containsString("Győr-Moson-Sopron")))
                .andExpect(content().string(containsString("Nyugat-Dunántúl")))
                .andExpect(content().string(containsString(">5</td>")))
                .andExpect(content().string(containsString(">2000</td>")))
                .andExpect(content().string(containsString(">2005</td>")))
                .andExpect(content().string(containsString("/abc123/assets/css/main.css")));
    }

    @Test
    void reportsEmptyDatabase() throws Exception {
        mockMvc.perform(get("/adatbazis"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Az adatbázisban még nincs széltoronyadat.")));
    }

    @Test
    void diagramSumsTowersAcrossLocationsWithinEachCounty() throws Exception {
        jdbcTemplate.update("INSERT INTO megye (id, nev, regio) VALUES (15, 'Győr-Moson-Sopron', 'Nyugat-Dunántúl')");
        jdbcTemplate.update("INSERT INTO megye (id, nev, regio) VALUES (16, 'Vas', 'Nyugat-Dunántúl')");
        jdbcTemplate.update("INSERT INTO helyszin (id, nev, megyeid) VALUES (4, 'Mosonmagyaróvár', 15)");
        jdbcTemplate.update("INSERT INTO helyszin (id, nev, megyeid) VALUES (17, 'Levél', 15)");
        jdbcTemplate.update("INSERT INTO helyszin (id, nev, megyeid) VALUES (9, 'Vép', 16)");
        jdbcTemplate.update("INSERT INTO torony (id, darab, teljesitmeny, kezdev, helyszinid) VALUES (10, 5, 2000, 2005, 4)");
        jdbcTemplate.update("INSERT INTO torony (id, darab, teljesitmeny, kezdev, helyszinid) VALUES (17, 12, 2000, 2006, 17)");
        jdbcTemplate.update("INSERT INTO torony (id, darab, teljesitmeny, kezdev, helyszinid) VALUES (9, 1, 600, 2005, 9)");

        mockMvc.perform(get("/abc123/diagram").contextPath("/abc123"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Győr-Moson-Sopron")))
                .andExpect(content().string(containsString(">17</td>")))
                .andExpect(content().string(containsString(">Vas</td>")))
                .andExpect(content().string(containsString(">1</td>")))
                .andExpect(content().string(containsString("/abc123/vendor/chartjs/chart.umd.min.js")));
    }

    @Test
    void diagramReportsEmptyDatabase() throws Exception {
        mockMvc.perform(get("/diagram"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Az adatbázisban még nincs diagramhoz használható széltoronyadat.")));
    }

    @Test
    void apiReturnsTowersWithLocationAndCounty() throws Exception {
        jdbcTemplate.update("INSERT INTO megye (id, nev, regio) VALUES (15, 'Győr-Moson-Sopron', 'Nyugat-Dunántúl')");
        jdbcTemplate.update("INSERT INTO helyszin (id, nev, megyeid) VALUES (4, 'Mosonmagyaróvár', 15)");
        jdbcTemplate.update("INSERT INTO torony (id, darab, teljesitmeny, kezdev, helyszinid) VALUES (10, 5, 2000, 2005, 4)");
        jdbcTemplate.update("INSERT INTO torony (id, darab, teljesitmeny, kezdev, helyszinid) VALUES (13, 5, 2000, 2006, 4)");

        mockMvc.perform(get("/abc123/api/tornyok").contextPath("/abc123"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[1].id").value(13))
                .andExpect(jsonPath("$[0].darab").value(5))
                .andExpect(jsonPath("$[0].teljesitmeny").value(2000))
                .andExpect(jsonPath("$[0].kezdev").value(2005))
                .andExpect(jsonPath("$[0].helyszinId").value(4))
                .andExpect(jsonPath("$[0].helyszin").value("Mosonmagyaróvár"))
                .andExpect(jsonPath("$[0].megyeId").value(15))
                .andExpect(jsonPath("$[0].megye").value("Győr-Moson-Sopron"))
                .andExpect(jsonPath("$[0].regio").value("Nyugat-Dunántúl"));

        mockMvc.perform(get("/api/tornyok/13"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(13))
                .andExpect(jsonPath("$.helyszin").value("Mosonmagyaróvár"));
        mockMvc.perform(get("/api/tornyok/999"))
                .andExpect(status().isNotFound());
        mockMvc.perform(post("/api/tornyok").with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    void emptyApiReturnsAnEmptyArray() throws Exception {
        mockMvc.perform(get("/api/tornyok"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
