package org.example.javalaborbeadando.torony;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Profile("mysql")
public class AdatbazisController {

    private final ToronyRepository toronyRepository;

    public AdatbazisController(ToronyRepository toronyRepository) {
        this.toronyRepository = toronyRepository;
    }

    @GetMapping("/adatbazis")
    public String adatbazis(Model model) {
        model.addAttribute("tornyok", toronyRepository.findAllByOrderByIdAsc());
        model.addAttribute("adatbazisElerheto", true);
        return "adatbazis";
    }
}
