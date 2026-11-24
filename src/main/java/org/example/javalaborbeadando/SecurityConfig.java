package org.example.javalaborbeadando;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/", "/adatbazis", "/kapcsolat", "/diagram", "/css/**", "/assets/**", "/js/**", "/vendor/**", "/error").permitAll()
                .anyRequest().denyAll())
                .build();
    }
}
