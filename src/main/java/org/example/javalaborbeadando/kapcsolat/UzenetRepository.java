package org.example.javalaborbeadando.kapcsolat;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UzenetRepository extends JpaRepository<Uzenet, Long> {
    List<Uzenet> findAllByOrderByKuldesIdejeDescIdDesc();
}
