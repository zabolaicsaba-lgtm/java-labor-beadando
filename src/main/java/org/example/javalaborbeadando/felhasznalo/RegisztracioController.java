package org.example.javalaborbeadando.felhasznalo;

import jakarta.validation.Valid;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@Profile("mysql")
public class RegisztracioController {
    private final FelhasznaloRepository repository;
    private final PasswordEncoder passwordEncoder;

    public RegisztracioController(FelhasznaloRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/regisztracio")
    public String oldal(Model model) {
        model.addAttribute("urlap", new RegisztracioUrlap());
        model.addAttribute("adatbazisElerheto", true);
        return "regisztracio";
    }

    @PostMapping("/regisztracio")
    public String regisztracio(@Valid @ModelAttribute("urlap") RegisztracioUrlap urlap,
                               BindingResult hibak, Model model, RedirectAttributes redirectAttributes) {
        if (!hibak.hasErrors() && repository.findByUsername(urlap.getUsername()).isPresent()) {
            hibak.rejectValue("username", "foglalt", "Ez a felhasználónév már foglalt.");
        }
        if (!hibak.hasErrors()) {
            try {
                repository.saveAndFlush(new Felhasznalo(urlap.getUsername(), passwordEncoder.encode(urlap.getPassword()), "USER"));
                redirectAttributes.addFlashAttribute("regisztralt", true);
                return "redirect:/login";
            } catch (DataIntegrityViolationException ex) {
                if (repository.findByUsername(urlap.getUsername()).isEmpty()) {
                    throw ex;
                }
                hibak.rejectValue("username", "foglalt", "Ez a felhasználónév már foglalt.");
            }
        }
        urlap.setPassword("");
        model.addAttribute("adatbazisElerheto", true);
        return "regisztracio";
    }
}
