package org.example.javalaborbeadando.kapcsolat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "uzenet")
public class Uzenet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nev;

    @Column(nullable = false, length = 254)
    private String email;

    @Column(nullable = false, length = 4000)
    private String szoveg;

    @Column(name = "kuldes_ideje", nullable = false)
    private LocalDateTime kuldesIdeje;

    protected Uzenet() {
    }

    public Uzenet(String nev, String email, String szoveg, LocalDateTime kuldesIdeje) {
        this.nev = nev;
        this.email = email;
        this.szoveg = szoveg;
        this.kuldesIdeje = kuldesIdeje;
    }

    public Long getId() {
        return id;
    }

    public String getNev() {
        return nev;
    }

    public String getEmail() {
        return email;
    }

    public String getSzoveg() {
        return szoveg;
    }

    public LocalDateTime getKuldesIdeje() {
        return kuldesIdeje;
    }
}
