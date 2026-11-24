package org.example.javalaborbeadando;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class JavaLaborBeadandoApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void homeIsPublicAndUsesTheDeploymentContextPath() throws Exception {
        mockMvc.perform(get("/abc123/").contextPath("/abc123"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Szélerőművek Magyarországon")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("/abc123/assets/css/main.css")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("HTML5 UP")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("/abc123/css/style.css")));
    }

    @Test
    void stylesheetIsPublic() throws Exception {
        mockMvc.perform(get("/css/style.css"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("#header.alt")));
        mockMvc.perform(get("/assets/css/main.css"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Stellar by HTML5 UP")));
        mockMvc.perform(get("/assets/webfonts/fa-solid-900.woff2"))
                .andExpect(status().isOk());
    }

    @Test
    void unfinishedRoutesAreNotPublic() throws Exception {
        mockMvc.perform(get("/admin"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void databasePageReportsWhenDatabaseProfileIsDisabled() throws Exception {
        mockMvc.perform(get("/abc123/adatbazis").contextPath("/abc123"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Az adatbázis jelenleg nem érhető el")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("/abc123/")));
    }

    @Test
    void contactPageReportsWhenDatabaseProfileIsDisabled() throws Exception {
        mockMvc.perform(get("/kapcsolat"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Az üzenetküldés jelenleg nem érhető el.")));
    }

    @Test
    void diagramReportsWhenDatabaseProfileIsDisabled() throws Exception {
        mockMvc.perform(get("/diagram"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("A diagram jelenleg nem érhető el.")));
        mockMvc.perform(get("/vendor/chartjs/chart.umd.min.js"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/js/diagram.js"))
                .andExpect(status().isOk());
    }

    @Test
    void restPageIsPublicAndApiReportsUnavailableDatabase() throws Exception {
        mockMvc.perform(get("/abc123/rest").contextPath("/abc123"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("/abc123/api/tornyok")));
        mockMvc.perform(get("/api/tornyok"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(content().contentTypeCompatibleWith("application/json"));
        mockMvc.perform(get("/api/tornyok/1"))
                .andExpect(status().isServiceUnavailable());
    }

    @Test
    void registrationAndLoginIndicateUnavailableDatabase() throws Exception {
        mockMvc.perform(get("/regisztracio"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("A regisztráció jelenleg nem érhető el.")));
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("A bejelentkezés jelenleg nem érhető el.")));
    }

    @Test
    void messagesIndicateUnavailableDatabaseForAuthenticatedUsers() throws Exception {
        mockMvc.perform(get("/uzenetek").with(
                        org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("teszt").roles("USER")))
                .andExpect(status().isServiceUnavailable())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Az üzenetek jelenleg nem érhetők el.")));
    }
}
