package org.example.javalaborbeadando.torony;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.example.javalaborbeadando.helyszin.Helyszin;
import org.example.javalaborbeadando.helyszin.HelyszinRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.NoSuchElementException;

@Service
@Validated
@Profile("mysql")
public class ToronySzolgaltatas {

    private final ToronyRepository tornyok;
    private final HelyszinRepository helyszinek;

    public ToronySzolgaltatas(ToronyRepository tornyok, HelyszinRepository helyszinek) {
        this.tornyok = tornyok;
        this.helyszinek = helyszinek;
    }

    @Transactional
    public Integer letrehozas(@NotNull @Valid ToronyAdatok adatok) {
        Helyszin helyszin = helyszinKereses(adatok.helyszinId());
        return tornyok.save(new Torony(adatok.darab(), adatok.teljesitmeny(),
                adatok.kezdev(), helyszin)).getId();
    }

    @Transactional
    public void modositas(@NotNull Integer id, @NotNull @Valid ToronyAdatok adatok) {
        Torony torony = toronyKereses(id);
        Helyszin helyszin = helyszinKereses(adatok.helyszinId());
        torony.modositas(adatok.darab(), adatok.teljesitmeny(), adatok.kezdev(), helyszin);
    }

    @Transactional
    public void torles(@NotNull Integer id) {
        tornyok.delete(toronyKereses(id));
    }

    private Torony toronyKereses(Integer id) {
        return tornyok.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Nem létező toronyazonosító: " + id));
    }

    private Helyszin helyszinKereses(Integer id) {
        return helyszinek.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Nem létező helyszínazonosító: " + id));
    }
}
