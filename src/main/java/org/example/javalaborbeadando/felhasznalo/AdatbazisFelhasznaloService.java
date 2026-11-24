package org.example.javalaborbeadando.felhasznalo;

import org.springframework.context.annotation.Profile;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Profile("mysql")
public class AdatbazisFelhasznaloService implements UserDetailsService {

    private final FelhasznaloRepository repository;

    public AdatbazisFelhasznaloService(FelhasznaloRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        Felhasznalo felhasznalo = repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Felhasználó nem található."));
        if (!felhasznalo.getSzerepkor().equals("USER") && !felhasznalo.getSzerepkor().equals("ADMIN")) {
            throw new UsernameNotFoundException("Érvénytelen felhasználói szerepkör.");
        }
        return User.withUsername(felhasznalo.getUsername())
                .password(felhasznalo.getJelszoHash())
                .roles(felhasznalo.getSzerepkor())
                .build();
    }
}
