package org.example.javalaborbeadando.kapcsolat;

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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:uzenetek;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.open-in-view=false"
})
@ActiveProfiles("mysql")
@AutoConfigureMockMvc
class UzenetekControllerTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private JdbcTemplate jdbc;

    @BeforeEach
    void clearMessages() {
        jdbc.update("DELETE FROM uzenet");
    }

    @Test
    void requiresLoginAndHidesMenuFromAnonymousUsers() throws Exception {
        mockMvc.perform(get("/uzenetek")).andExpect(status().is3xxRedirection());
        mockMvc.perform(get("/")).andExpect(content().string(not(containsString("href=\"/uzenetek\""))));
        mockMvc.perform(get("/uzenetek").with(user("masik").roles("OTHER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void bothRolesSeeMessagesNewestFirstAndHtmlIsEscaped() throws Exception {
        jdbc.update("INSERT INTO uzenet (nev, email, szoveg, kuldes_ideje) VALUES (?, ?, ?, ?)",
                "Régi", "regi@example.hu", "régebbi", java.sql.Timestamp.valueOf("2025-01-01 10:00:00"));
        jdbc.update("INSERT INTO uzenet (nev, email, szoveg, kuldes_ideje) VALUES (?, ?, ?, ?)",
                "Új", "uj@example.hu", "<script>veszely</script>", java.sql.Timestamp.valueOf("2025-02-01 11:30:00"));

        String html = mockMvc.perform(get("/abc123/uzenetek").contextPath("/abc123").with(user("latogato").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("2025.02.01. 11:30:00")))
                .andExpect(content().string(containsString("uj@example.hu")))
                .andExpect(content().string(containsString("&lt;script&gt;veszely&lt;/script&gt;")))
                .andReturn().getResponse().getContentAsString(java.nio.charset.StandardCharsets.UTF_8);
        assertThat(html.indexOf("uj@example.hu")).isLessThan(html.indexOf("regi@example.hu"));
        mockMvc.perform(get("/uzenetek").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk());
        mockMvc.perform(get("/").with(user("latogato").roles("USER")))
                .andExpect(content().string(containsString("href=\"/uzenetek\"")));
    }

    @Test
    void emptyListHasClearMessage() throws Exception {
        mockMvc.perform(get("/uzenetek").with(user("latogato").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Még nem érkezett üzenet.")));
    }
}
