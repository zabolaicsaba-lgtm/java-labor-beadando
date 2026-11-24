package org.example.javalaborbeadando.helyszin;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HelyszinRepository extends JpaRepository<Helyszin, Integer> {
    List<Helyszin> findAllByOrderByNevAscIdAsc();
}
