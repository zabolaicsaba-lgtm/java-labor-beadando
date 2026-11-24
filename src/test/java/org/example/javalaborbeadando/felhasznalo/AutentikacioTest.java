package org.example.javalaborbeadando.felhasznalo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:auth;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.open-in-view=false"
})
@ActiveProfiles("mysql")
@AutoConfigureMockMvc
class AutentikacioTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private JdbcTemplate jdbc;
    @Autowired private PasswordEncoder encoder;

    @BeforeEach
    void clearUsers() {
        jdbc.update("DELETE FROM felhasznalo");
    }

    @Test
    void registrationLoginAndLogout() throws Exception {
        mockMvc.perform(get("/abc123/regisztracio").contextPath("/abc123"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("/abc123/regisztracio")));
        mockMvc.perform(post("/abc123/regisztracio").contextPath("/abc123").with(csrf())
                        .param("username", "elek").param("password", "password123").param("szerepkor", "ADMIN"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/abc123/login"));
        String hash = jdbc.queryForObject("SELECT jelszo_hash FROM felhasznalo WHERE username = 'elek'", String.class);
        assertThat(hash).isNotEqualTo("password123");
        assertThat(encoder.matches("password123", hash)).isTrue();
        assertThat(jdbc.queryForObject("SELECT szerepkor FROM felhasznalo WHERE username = 'elek'", String.class)).isEqualTo("USER");

        var result = mockMvc.perform(formLogin().user("elek").password("password123"))
                .andExpect(authenticated().withUsername("elek").withRoles("USER"))
                .andReturn();
        var session = (org.springframework.mock.web.MockHttpSession) result.getRequest().getSession(false);
        mockMvc.perform(get("/").session(session))
                .andExpect(content().string(containsString("Kijelentkezés")));
        mockMvc.perform(post("/logout").session(session).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(unauthenticated());
        mockMvc.perform(formLogin().user("elek").password("rosszjelszo"))
                .andExpect(unauthenticated());
    }

    @Test
    void invalidAndDuplicateRegistrationsAreRejected() throws Exception {
        mockMvc.perform(post("/regisztracio").with(csrf()).param("username", "ADMIN").param("password", "short"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("A felhasználónév 3")))
                .andExpect(content().string(containsString("A jelszó 8")));
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM felhasznalo", Integer.class)).isZero();
        jdbc.update("INSERT INTO felhasznalo (username, jelszo_hash, szerepkor) VALUES (?, ?, ?)", "elek", encoder.encode("password123"), "USER");
        mockMvc.perform(post("/regisztracio").with(csrf()).param("username", "elek").param("password", "password123"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Ez a felhasználónév már foglalt.")))
                .andExpect(content().string(org.hamcrest.Matchers.not(containsString("value=\"password123\""))));
        mockMvc.perform(post("/regisztracio").param("username", "masik").param("password", "password123"))
                .andExpect(status().isForbidden());
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM felhasznalo", Integer.class)).isEqualTo(1);
    }

    @Test
    void storedAdminRoleIsRecognized() throws Exception {
        jdbc.update("INSERT INTO felhasznalo (username, jelszo_hash, szerepkor) VALUES (?, ?, ?)", "admin", encoder.encode("password123"), "ADMIN");
        mockMvc.perform(formLogin().user("admin").password("password123"))
                .andExpect(authenticated().withRoles("ADMIN"));
    }
}
