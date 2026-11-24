package org.example.javalaborbeadando.felhasznalo;

import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BelepesController {
    private final Environment environment;

    public BelepesController(Environment environment) {
        this.environment = environment;
    }

    @GetMapping("/login")
    public String login(Model model) {
        boolean elerheto = environment.acceptsProfiles(Profiles.of("mysql"));
        model.addAttribute("adatbazisElerheto", elerheto);
        return "login";
    }
}
