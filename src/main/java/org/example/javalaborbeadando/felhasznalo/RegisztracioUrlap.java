package org.example.javalaborbeadando.felhasznalo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RegisztracioUrlap {
    @NotBlank(message = "Add meg a felhasználónevet.")
    @Pattern(regexp = "[a-z0-9._-]{3,50}", message = "A felhasználónév 3–50 karakteres lehet: kisbetű, szám, pont, kötőjel vagy aláhúzás.")
    private String username;

    @NotBlank(message = "Add meg a jelszót.")
    @Size(min = 8, max = 72, message = "A jelszó 8–72 karakteres legyen.")
    @Pattern(regexp = "[\\x20-\\x7E]*", message = "A jelszó csak ASCII karaktereket tartalmazhat.")
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
