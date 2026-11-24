package org.example.javalaborbeadando.torony;

import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Profile("!mysql")
public class ToronyApiNemElerhetoController {

    @GetMapping({"/api/tornyok", "/api/tornyok/{id}"})
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public Map<String, String> nemElerheto() {
        return Map.of("hiba", "Az adatbázis jelenleg nem érhető el.");
    }
}
