package org.example.javalaborbeadando;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

@Configuration
public class SecurityConfig {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Profile("!mysql")
    UserDetailsService unavailableUsers() {
        return username -> {
            throw new UsernameNotFoundException("Az adatbázis nem elérhető.");
        };
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/", "/adatbazis", "/kapcsolat", "/diagram", "/rest", "/login", "/regisztracio", "/css/**", "/assets/**", "/js/**", "/vendor/**", "/error").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/tornyok", "/api/tornyok/*").permitAll()
                .requestMatchers(HttpMethod.GET, "/uzenetek").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/admin", "/admin/**").hasRole("ADMIN")
                .anyRequest().denyAll())
                .exceptionHandling(exceptions -> exceptions.defaultAuthenticationEntryPointFor(
                        new HttpStatusEntryPoint(HttpStatus.FORBIDDEN),
                        PathPatternRequestMatcher.withDefaults().matcher("/api/**"))
                        .defaultAuthenticationEntryPointFor(new LoginUrlAuthenticationEntryPoint("/login"),
                                PathPatternRequestMatcher.withDefaults().matcher("/uzenetek"))
                        .defaultAuthenticationEntryPointFor(new LoginUrlAuthenticationEntryPoint("/login"),
                                PathPatternRequestMatcher.withDefaults().matcher("/admin/**")))
                .formLogin(form -> form.loginPage("/login").defaultSuccessUrl("/", false).permitAll())
                .logout(logout -> logout.logoutSuccessUrl("/").permitAll())
                .build();
    }
}
