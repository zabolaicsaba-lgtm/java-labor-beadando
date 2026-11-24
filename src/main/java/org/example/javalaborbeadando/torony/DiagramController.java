package org.example.javalaborbeadando.torony;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Profile("mysql")
public class DiagramController {

    private final ToronyRepository repository;

    public DiagramController(ToronyRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/diagram")
    public String diagram(Model model) {
        model.addAttribute("adatbazisElerheto", true);
        model.addAttribute("osszesites", repository.osszesitesMegyenkent());
        return "diagram";
    }
}
