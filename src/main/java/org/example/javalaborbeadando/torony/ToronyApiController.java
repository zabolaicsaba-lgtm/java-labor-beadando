package org.example.javalaborbeadando.torony;

import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tornyok")
@Profile("mysql")
public class ToronyApiController {

    private final ToronyRepository repository;

    public ToronyApiController(ToronyRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<ToronyValasz> listazas() {
        return repository.findAllByOrderByIdAsc().stream().map(ToronyValasz::from).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ToronyValasz> egyTorony(@PathVariable Integer id) {
        return ResponseEntity.of(repository.findWithHelyszinById(id).map(ToronyValasz::from));
    }
}
