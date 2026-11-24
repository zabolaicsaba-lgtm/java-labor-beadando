package org.example.javalaborbeadando.torony;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:admincrud;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.open-in-view=false"
})
@ActiveProfiles("mysql")
@AutoConfigureMockMvc
class AdminToronyControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private JdbcTemplate jdbc;

    @BeforeEach
    void setup() {
        jdbc.update("DELETE FROM torony");
        jdbc.update("DELETE FROM helyszin");
        jdbc.update("DELETE FROM megye");
        jdbc.update("INSERT INTO megye (id, nev, regio) VALUES (15, 'Győr-Moson-Sopron', 'Nyugat-Dunántúl')");
        jdbc.update("INSERT INTO helyszin (id, nev, megyeid) VALUES (4, 'Mosonmagyaróvár', 15)");
    }

    @Test
    void accessAndMenuAreLimitedToAdmin() throws Exception {
        mockMvc.perform(get("/admin")).andExpect(status().is3xxRedirection());
        mockMvc.perform(get("/admin").with(user("user").roles("USER"))).andExpect(status().isForbidden());
        mockMvc.perform(post("/admin/uj").with(user("user").roles("USER")).with(csrf()))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/").with(user("user").roles("USER")))
                .andExpect(content().string(not(containsString("href=\"/admin\""))));
        mockMvc.perform(get("/").with(user("admin").roles("ADMIN")))
                .andExpect(content().string(containsString("href=\"/admin\"")));
        mockMvc.perform(get("/admin").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    void adminCanCreateEditAndDeleteWithContextPath() throws Exception {
        mockMvc.perform(get("/abc123/admin/uj").contextPath("/abc123").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Mosonmagyaróvár")))
                .andExpect(content().string(containsString("action=\"/abc123/admin/uj\"")));
        mockMvc.perform(post("/abc123/admin/uj").contextPath("/abc123").with(user("admin").roles("ADMIN")).with(csrf())
                        .param("darab", "2").param("teljesitmeny", "2000").param("kezdev", "2010").param("helyszinId", "4"))
                .andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/abc123/admin"));
        Integer id = jdbc.queryForObject("SELECT id FROM torony", Integer.class);
        mockMvc.perform(get("/admin/" + id + "/szerkesztes").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk()).andExpect(content().string(containsString("value=\"2000\"")));
        mockMvc.perform(post("/admin/" + id + "/szerkesztes").with(user("admin").roles("ADMIN")).with(csrf())
                        .param("darab", "3").param("teljesitmeny", "3000").param("kezdev", "2011").param("helyszinId", "4"))
                .andExpect(status().is3xxRedirection());
        assertThat(jdbc.queryForObject("SELECT darab FROM torony WHERE id = ?", Integer.class, id)).isEqualTo(3);
        mockMvc.perform(get("/admin").with(user("admin").roles("ADMIN")))
                .andExpect(content().string(containsString("Mosonmagyaróvár")))
                .andExpect(content().string(containsString(">3000</td>")))
                .andExpect(content().string(containsString("href=\"/admin/" + id + "/torles\"")));
        mockMvc.perform(get("/abc123/admin/" + id + "/torles").contextPath("/abc123").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Mosonmagyaróvár")))
                .andExpect(content().string(containsString("Győr-Moson-Sopron")))
                .andExpect(content().string(containsString("action=\"/abc123/admin/" + id + "/torles\"")))
                .andExpect(content().string(containsString("href=\"/abc123/admin\"")))
                .andExpect(content().string(containsString("name=\"_csrf\"")));
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM torony WHERE id = ?", Integer.class, id)).isEqualTo(1);
        mockMvc.perform(get("/admin/" + id + "/torles").with(user("user").roles("USER")))
                .andExpect(status().isForbidden());
        mockMvc.perform(post("/admin/" + id + "/torles").with(user("admin").roles("ADMIN")))
                .andExpect(status().isForbidden());
        mockMvc.perform(post("/admin/" + id + "/torles").with(user("admin").roles("ADMIN")).with(csrf()))
                .andExpect(status().is3xxRedirection());
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM torony", Integer.class)).isZero();
    }

    @Test
    void invalidInputAndMissingRecordsAreHandled() throws Exception {
        mockMvc.perform(post("/admin/uj").with(user("admin").roles("ADMIN")).with(csrf())
                        .param("darab", "0").param("teljesitmeny", "2000").param("kezdev", "2010").param("helyszinId", "999"))
                .andExpect(status().isOk()).andExpect(content().string(containsString("Válassz létező helyszínt.")));
        mockMvc.perform(post("/admin/uj").with(user("admin").roles("ADMIN")).with(csrf())
                        .param("darab", "nemszam").param("teljesitmeny", "2000").param("kezdev", "2010").param("helyszinId", "4"))
                .andExpect(status().isOk());
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM torony", Integer.class)).isZero();
        mockMvc.perform(get("/admin/999/szerkesztes").with(user("admin").roles("ADMIN")))
                .andExpect(status().isNotFound());
        mockMvc.perform(get("/admin/999/torles").with(user("admin").roles("ADMIN")))
                .andExpect(status().isNotFound());
        mockMvc.perform(post("/admin/999/torles").with(user("admin").roles("ADMIN")).with(csrf()))
                .andExpect(status().isNotFound());
    }
}
