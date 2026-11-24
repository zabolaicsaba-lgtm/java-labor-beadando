package org.example.javalaborbeadando.torony;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ToronyRepository extends JpaRepository<Torony, Integer> {

    @EntityGraph(attributePaths = {"helyszin", "helyszin.megye"})
    List<Torony> findAllByOrderByIdAsc();

    @Query("""
            select new org.example.javalaborbeadando.torony.MegyeiToronyszam(
                m.nev, sum(t.darab))
            from Torony t
            join t.helyszin h
            join h.megye m
            group by m.id, m.nev
            order by m.nev, m.id
            """)
    List<MegyeiToronyszam> osszesitesMegyenkent();
}
