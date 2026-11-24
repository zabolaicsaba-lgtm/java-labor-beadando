package org.example.javalaborbeadando.helyszin;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.example.javalaborbeadando.megye.Megye;

@Entity
@Table(name = "helyszin")
public class Helyszin {

    @Id
    private Integer id;

    @Column(nullable = false, length = 100)
    private String nev;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "megyeid", nullable = false)
    private Megye megye;

    protected Helyszin() {
    }

    public Integer getId() {
        return id;
    }

    public String getNev() {
        return nev;
    }

    public Megye getMegye() {
        return megye;
    }
}
