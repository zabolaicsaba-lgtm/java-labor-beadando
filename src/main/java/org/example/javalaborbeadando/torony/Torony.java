package org.example.javalaborbeadando.torony;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.example.javalaborbeadando.helyszin.Helyszin;

@Entity
@Table(name = "torony")
public class Torony {

    @Id
    private Integer id;

    @Column(nullable = false)
    private Integer darab;

    @Column(nullable = false)
    private Integer teljesitmeny;

    @Column(nullable = false)
    private Integer kezdev;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "helyszinid", nullable = false)
    private Helyszin helyszin;

    protected Torony() {
    }

    public Integer getId() {
        return id;
    }

    public Integer getDarab() {
        return darab;
    }

    public Integer getTeljesitmeny() {
        return teljesitmeny;
    }

    public Integer getKezdev() {
        return kezdev;
    }

    public Helyszin getHelyszin() {
        return helyszin;
    }
}
