package org.example.javalaborbeadando.kapcsolat;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class KapcsolatUrlap {

    @NotBlank(message = "Add meg a nevedet.")
    @Size(max = 100, message = "A név legfeljebb 100 karakter lehet.")
    private String nev;

    @NotBlank(message = "Add meg az e-mail-címedet.")
    @Email(message = "Adj meg egy érvényes e-mail-címet.")
    @Size(max = 254, message = "Az e-mail-cím legfeljebb 254 karakter lehet.")
    private String email;

    @NotBlank(message = "Írd meg az üzenetedet.")
    @Size(max = 4000, message = "Az üzenet legfeljebb 4000 karakter lehet.")
    private String szoveg;

    public String getNev() {
        return nev;
    }

    public void setNev(String nev) {
        this.nev = nev;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSzoveg() {
        return szoveg;
    }

    public void setSzoveg(String szoveg) {
        this.szoveg = szoveg;
    }
}
