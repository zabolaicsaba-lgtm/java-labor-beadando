package org.example.javalaborbeadando.felhasznalo;

import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
@Profile("!mysql")
public class RegisztracioNemElerhetoController {
    @RequestMapping(value = "/regisztracio", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public String oldal(Model model) {
        model.addAttribute("adatbazisElerheto", false);
        return "regisztracio";
    }
}
