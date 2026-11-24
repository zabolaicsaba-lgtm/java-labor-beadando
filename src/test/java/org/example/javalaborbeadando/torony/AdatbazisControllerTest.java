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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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
}
