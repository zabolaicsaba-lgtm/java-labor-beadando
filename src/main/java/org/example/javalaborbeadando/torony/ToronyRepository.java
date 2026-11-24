package org.example.javalaborbeadando.torony;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.List;

public interface ToronyRepository extends JpaRepository<Torony, Integer> {

    @EntityGraph(attributePaths = {"helyszin", "helyszin.megye"})
    List<Torony> findAllByOrderByIdAsc();
}
