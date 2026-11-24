package org.example.javalaborbeadando.felhasznalo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "felhasznalo")
public class Felhasznalo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "jelszo_hash", nullable = false, length = 100)
    private String jelszoHash;

    @Column(nullable = false, length = 20)
    private String szerepkor;

    protected Felhasznalo() {
    }

    public Felhasznalo(String username, String jelszoHash, String szerepkor) {
        this.username = username;
        this.jelszoHash = jelszoHash;
        this.szerepkor = szerepkor;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getJelszoHash() {
        return jelszoHash;
    }

    public String getSzerepkor() {
        return szerepkor;
    }
}
