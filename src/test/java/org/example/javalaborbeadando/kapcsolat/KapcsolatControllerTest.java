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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:kapcsolat;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.open-in-view=false"
})
@ActiveProfiles("mysql")
@AutoConfigureMockMvc
class KapcsolatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void clearMessages() {
        jdbcTemplate.update("DELETE FROM uzenet");
    }

    @Test
    void formIsPublicAndUsesContextPath() throws Exception {
        mockMvc.perform(get("/abc123/kapcsolat").contextPath("/abc123"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("action=\"/abc123/kapcsolat\"")))
                .andExpect(content().string(containsString("bejelentkezett felhasználója láthatja")))
                .andExpect(content().string(containsString("Ne adj meg bizalmas adatokat.")))
                .andExpect(content().string(containsString("aria-describedby=\"uzenet-tajekoztato\"")))
                .andExpect(content().string(containsString("name=\"_csrf\"")));
    }

    @Test
    void validMessageIsSavedAndRedirected() throws Exception {
        mockMvc.perform(post("/abc123/kapcsolat").contextPath("/abc123").with(csrf())
                        .param("nev", "  Teszt Elek  ")
                        .param("email", "elek@example.hu")
                        .param("szoveg", "Üdvözlet!"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/abc123/kapcsolat"));

        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM uzenet", Integer.class)).isEqualTo(1);
        assertThat(jdbcTemplate.queryForObject("SELECT nev FROM uzenet", String.class)).isEqualTo("Teszt Elek");
        assertThat(jdbcTemplate.queryForObject("SELECT email FROM uzenet", String.class)).isEqualTo("elek@example.hu");
        assertThat(jdbcTemplate.queryForObject("SELECT szoveg FROM uzenet", String.class)).isEqualTo("Üdvözlet!");
        assertThat(jdbcTemplate.queryForObject("SELECT kuldes_ideje FROM uzenet", java.sql.Timestamp.class)).isNotNull();
    }

    @Test
    void invalidInputShowsErrorsWithoutSaving() throws Exception {
        mockMvc.perform(post("/kapcsolat").with(csrf())
                        .param("nev", " ")
                        .param("email", "hibas-email")
                        .param("szoveg", " "))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Add meg a nevedet.")))
                .andExpect(content().string(containsString("bejelentkezett felhasználója láthatja")))
                .andExpect(content().string(containsString("Adj meg egy érvényes e-mail-címet.")))
                .andExpect(content().string(containsString("Írd meg az üzenetedet.")));

        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM uzenet", Integer.class)).isZero();
    }

    @Test
    void overlyLongInputIsRejected() throws Exception {
        mockMvc.perform(post("/kapcsolat").with(csrf())
                        .param("nev", "x".repeat(101))
                        .param("email", "elek@example.hu")
                        .param("szoveg", "x".repeat(4001)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("A név legfeljebb 100 karakter lehet.")))
                .andExpect(content().string(containsString("Az üzenet legfeljebb 4000 karakter lehet.")));

        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM uzenet", Integer.class)).isZero();
    }

    @Test
    void csrfIsRequired() throws Exception {
        mockMvc.perform(post("/kapcsolat")
                        .param("nev", "Teszt")
                        .param("email", "elek@example.hu")
                        .param("szoveg", "Üzenet"))
                .andExpect(status().isForbidden());
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM uzenet", Integer.class)).isZero();
    }
}
