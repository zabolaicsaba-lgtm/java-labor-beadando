package org.example.javalaborbeadando.torony;

import jakarta.validation.Valid;
import org.example.javalaborbeadando.helyszin.HelyszinRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.NoSuchElementException;

@Controller
@Profile("mysql")
public class AdminToronyController {

    private final ToronyRepository tornyok;
    private final HelyszinRepository helyszinek;
    private final ToronySzolgaltatas szolgaltatas;

    public AdminToronyController(ToronyRepository tornyok, HelyszinRepository helyszinek,
                                 ToronySzolgaltatas szolgaltatas) {
        this.tornyok = tornyok;
        this.helyszinek = helyszinek;
        this.szolgaltatas = szolgaltatas;
    }

    @GetMapping("/admin")
    public String lista(Model model) {
        model.addAttribute("tornyok", tornyok.findAllByOrderByIdAsc());
        model.addAttribute("adatbazisElerheto", true);
        return "admin-tornyok";
    }

    @GetMapping("/admin/uj")
    public String uj(Model model) {
        model.addAttribute("adatok", new ToronyAdatok(null, null, null, null));
        model.addAttribute("modositas", false);
        return urlap(model);
    }

    @GetMapping("/admin/{id}/szerkesztes")
    public String szerkesztes(@PathVariable Integer id, Model model) {
        Torony torony = tornyok.findWithHelyszinById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        model.addAttribute("adatok", new ToronyAdatok(torony.getDarab(), torony.getTeljesitmeny(),
                torony.getKezdev(), torony.getHelyszin().getId()));
        model.addAttribute("modositas", true);
        model.addAttribute("id", id);
        return urlap(model);
    }

    @PostMapping("/admin/uj")
    public String letrehozas(@Valid @ModelAttribute("adatok") ToronyAdatok adatok, BindingResult hibak,
                             Model model, RedirectAttributes redirect) {
        model.addAttribute("modositas", false);
        if (helyszinHibas(adatok, hibak)) {
            return urlap(model);
        }
        try {
            szolgaltatas.letrehozas(adatok);
        } catch (NoSuchElementException ex) {
            hibak.rejectValue("helyszinId", "nemletezik", "A kiválasztott helyszín már nem létezik.");
            return urlap(model);
        }
        redirect.addFlashAttribute("siker", "A széltoronyrekordot létrehoztuk.");
        return "redirect:/admin";
    }

    @PostMapping("/admin/{id}/szerkesztes")
    public String modositas(@PathVariable Integer id, @Valid @ModelAttribute("adatok") ToronyAdatok adatok,
                            BindingResult hibak, Model model, RedirectAttributes redirect) {
        if (!tornyok.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        model.addAttribute("modositas", true);
        model.addAttribute("id", id);
        if (helyszinHibas(adatok, hibak)) {
            return urlap(model);
        }
        try {
            szolgaltatas.modositas(id, adatok);
        } catch (NoSuchElementException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "A torony vagy a helyszín már nem létezik.", ex);
        }
        redirect.addFlashAttribute("siker", "A széltoronyrekordot módosítottuk.");
        return "redirect:/admin";
    }

    @PostMapping("/admin/{id}/torles")
    public String torles(@PathVariable Integer id, RedirectAttributes redirect) {
        try {
            szolgaltatas.torles(id);
        } catch (NoSuchElementException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "A torony már nem létezik.", ex);
        }
        redirect.addFlashAttribute("siker", "A széltoronyrekordot töröltük.");
        return "redirect:/admin";
    }

    private boolean helyszinHibas(ToronyAdatok adatok, BindingResult hibak) {
        if (!hibak.hasFieldErrors("helyszinId") && !helyszinek.existsById(adatok.helyszinId())) {
            hibak.rejectValue("helyszinId", "nemletezik", "Válassz létező helyszínt.");
        }
        return hibak.hasErrors();
    }

    private String urlap(Model model) {
        model.addAttribute("helyszinek", helyszinek.findAllByOrderByNevAscIdAsc());
        return "admin-torony-urlap";
    }
}
