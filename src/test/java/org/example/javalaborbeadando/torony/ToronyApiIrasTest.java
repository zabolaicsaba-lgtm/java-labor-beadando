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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:apiiras;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.open-in-view=false"
})
@ActiveProfiles("mysql")
@AutoConfigureMockMvc
class ToronyApiIrasTest {

    private static final String JSON = "{\"darab\":3,\"teljesitmeny\":2000,\"kezdev\":2010,\"helyszinId\":4}";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void seed() {
        jdbcTemplate.update("DELETE FROM torony");
        jdbcTemplate.update("DELETE FROM helyszin");
        jdbcTemplate.update("DELETE FROM megye");
        jdbcTemplate.update("INSERT INTO megye (id, nev, regio) VALUES (15, 'Győr-Moson-Sopron', 'Nyugat-Dunántúl')");
        jdbcTemplate.update("INSERT INTO helyszin (id, nev, megyeid) VALUES (4, 'Mosonmagyaróvár', 15)");
        jdbcTemplate.update("INSERT INTO torony (id, darab, teljesitmeny, kezdev, helyszinid) VALUES (10, 5, 2000, 2005, 4)");
    }

    private int towerCount() {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM torony", Integer.class);
    }

    @Test
    void adminCanCreateTower() throws Exception {
        mockMvc.perform(post("/api/tornyok").with(user("admin").roles("ADMIN"))
                        .contentType("application/json").content(JSON))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/tornyok/")))
                .andExpect(jsonPath("$.darab").value(3))
                .andExpect(jsonPath("$.helyszin").value("Mosonmagyaróvár"));
        assertEquals(2, towerCount());
    }

    @Test
    void regularUserCannotWrite() throws Exception {
        mockMvc.perform(post("/api/tornyok").with(user("u").roles("USER"))
                        .contentType("application/json").content(JSON))
                .andExpect(status().isForbidden());
        mockMvc.perform(put("/api/tornyok/10").with(user("u").roles("USER"))
                        .contentType("application/json").content(JSON))
                .andExpect(status().isForbidden());
        mockMvc.perform(delete("/api/tornyok/10").with(user("u").roles("USER")))
                .andExpect(status().isForbidden());
        assertEquals(1, towerCount());
    }

    @Test
    void anonymousCannotWrite() throws Exception {
        mockMvc.perform(delete("/api/tornyok/10")).andExpect(status().isUnauthorized());
        assertEquals(1, towerCount());
    }

    @Test
    void adminCanUpdateAndDelete() throws Exception {
        mockMvc.perform(put("/api/tornyok/10").with(user("admin").roles("ADMIN"))
                        .contentType("application/json").content(JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.darab").value(3))
                .andExpect(jsonPath("$.kezdev").value(2010));
        mockMvc.perform(delete("/api/tornyok/10").with(user("admin").roles("ADMIN")))
                .andExpect(status().isNoContent());
        assertEquals(0, towerCount());
    }

    @Test
    void missingResourcesAndBadInputAreRejected() throws Exception {
        mockMvc.perform(put("/api/tornyok/999").with(user("admin").roles("ADMIN"))
                        .contentType("application/json").content(JSON))
                .andExpect(status().isNotFound());
        mockMvc.perform(delete("/api/tornyok/999").with(user("admin").roles("ADMIN")))
                .andExpect(status().isNotFound());
        mockMvc.perform(post("/api/tornyok").with(user("admin").roles("ADMIN"))
                        .contentType("application/json")
                        .content("{\"darab\":-1,\"teljesitmeny\":1,\"kezdev\":2000,\"helyszinId\":4}"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post("/api/tornyok").with(user("admin").roles("ADMIN"))
                        .contentType("application/json")
                        .content("{\"darab\":1,\"teljesitmeny\":1,\"kezdev\":2000,\"helyszinId\":999}"))
                .andExpect(status().isBadRequest());
    }
}