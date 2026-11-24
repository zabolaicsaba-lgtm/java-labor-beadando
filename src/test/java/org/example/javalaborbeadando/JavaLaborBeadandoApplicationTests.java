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
                .andExpect(status().isForbidden());
    }
}
