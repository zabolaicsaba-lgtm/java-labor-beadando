package org.example.javalaborbeadando.kapcsolat;

import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
@Profile("!mysql")
public class UzenetekNemElerhetoController {
    @GetMapping("/uzenetek")
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public String uzenetek(Model model) {
        model.addAttribute("adatbazisElerheto", false);
        return "uzenetek";
    }
}
