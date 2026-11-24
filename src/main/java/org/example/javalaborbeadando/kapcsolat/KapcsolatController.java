package org.example.javalaborbeadando.kapcsolat;

import jakarta.validation.Valid;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

@Controller
@Profile("mysql")
public class KapcsolatController {

    private final UzenetRepository repository;

    public KapcsolatController(UzenetRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/kapcsolat")
    public String kapcsolat(Model model) {
        model.addAttribute("urlap", new KapcsolatUrlap());
        model.addAttribute("adatbazisElerheto", true);
        return "kapcsolat";
    }

    @PostMapping("/kapcsolat")
    public String kuldes(@Valid @ModelAttribute("urlap") KapcsolatUrlap urlap,
                         BindingResult hibak, Model model, RedirectAttributes redirectAttributes) {
        if (hibak.hasErrors()) {
            model.addAttribute("adatbazisElerheto", true);
            return "kapcsolat";
        }

        repository.save(new Uzenet(urlap.getNev().strip(), urlap.getEmail().strip(),
                urlap.getSzoveg().strip(), LocalDateTime.now()));
        redirectAttributes.addFlashAttribute("sikeresKuldes", true);
        return "redirect:/kapcsolat";
    }
}
