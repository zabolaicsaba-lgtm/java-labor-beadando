package org.example.javalaborbeadando.kapcsolat;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Profile("mysql")
public class UzenetekController {
    private final UzenetRepository repository;

    public UzenetekController(UzenetRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/uzenetek")
    public String uzenetek(Model model) {
        model.addAttribute("adatbazisElerheto", true);
        model.addAttribute("uzenetek", repository.findAllByOrderByKuldesIdejeDescIdDesc());
        return "uzenetek";
    }
}
