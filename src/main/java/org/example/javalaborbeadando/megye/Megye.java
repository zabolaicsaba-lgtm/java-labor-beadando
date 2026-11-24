package org.example.javalaborbeadando.megye;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "megye")
public class Megye {

    @Id
    private Integer id;

    @Column(nullable = false, length = 100)
    private String nev;

    @Column(nullable = false, length = 100)
    private String regio;

    protected Megye() {
    }

    public Integer getId() {
        return id;
    }

    public String getNev() {
        return nev;
    }

    public String getRegio() {
        return regio;
    }
}
