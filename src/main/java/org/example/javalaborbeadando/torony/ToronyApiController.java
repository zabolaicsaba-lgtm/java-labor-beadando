package org.example.javalaborbeadando.torony;

import jakarta.validation.Valid;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/tornyok")
@Profile("mysql")
public class ToronyApiController {

    private final ToronyRepository repository;
    private final ToronySzolgaltatas szolgaltatas;

    public ToronyApiController(ToronyRepository repository, ToronySzolgaltatas szolgaltatas) {
        this.repository = repository;
        this.szolgaltatas = szolgaltatas;
    }

    @GetMapping
    public List<ToronyValasz> listazas() {
        return repository.findAllByOrderByIdAsc().stream().map(ToronyValasz::from).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ToronyValasz> egyTorony(@PathVariable Integer id) {
        return ResponseEntity.of(repository.findWithHelyszinById(id).map(ToronyValasz::from));
    }

    @PostMapping
    public ResponseEntity<ToronyValasz> letrehozas(@Valid @RequestBody ToronyAdatok adatok) {
        try {
            Integer id = szolgaltatas.letrehozas(adatok);
            URI hely = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(id).toUri();
            return ResponseEntity.created(hely)
                    .body(repository.findWithHelyszinById(id).map(ToronyValasz::from).orElseThrow());
        } catch (NoSuchElementException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ToronyValasz> modositas(@PathVariable Integer id, @Valid @RequestBody ToronyAdatok adatok) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        try {
            szolgaltatas.modositas(id, adatok);
        } catch (NoSuchElementException e) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.of(repository.findWithHelyszinById(id).map(ToronyValasz::from));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> torles(@PathVariable Integer id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        szolgaltatas.torles(id);
        return ResponseEntity.noContent().build();
    }
}