package com.miAplicacion.demo.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // 1. Cifrado de Contraseñas
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 2. Definición de Usuarios en Memoria (PARA EMPEZAR)
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
        // Usuario de prueba: "user" con contraseña "password"
        UserDetails user = User.withUsername("user")
            .password(encoder.encode("password"))
            .roles("USER")
            .build();
        
        // Usuario de prueba: "admin" con contraseña "admin"
        UserDetails admin = User.withUsername("admin")
            .password(encoder.encode("admin"))
            .roles("ADMIN", "USER")
            .build();
        
        return new InMemoryUserDetailsManager(user, admin);
    }

    // 3. Configuración de Acceso a Rutas (Autorización)
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        
        http
            .csrf(csrf -> csrf.disable()) // Deshabilitar CSRF para APIs REST
            .authorizeHttpRequests((requests) -> requests
                // Permitir acceso sin autenticar a la página de login
                .requestMatchers("/login", "/login?error=true").permitAll()
                // Permitir acceso sin autenticar a la ruta de Proyectos MVC (ejemplo de ruta pública)
                .requestMatchers("/proyectos/**").permitAll() 
                // Permitir acceso sin autenticar a la API REST de Proyectos
                .requestMatchers("/api/proyectos/**").permitAll()
                // Permitir acceso a la página principal
                .requestMatchers("/", "/home").permitAll()
                // Requerir rol 'ADMIN' para todas las operaciones MVC en /empleados
                .requestMatchers("/empleados/**").hasRole("ADMIN") 
                // Requerir rol 'ADMIN' para todas las operaciones REST API en /api/empleados
                .requestMatchers("/api/empleados/**").hasRole("ADMIN")
                // Todas las demás peticiones requieren autenticación
                .anyRequest().authenticated() 
            )
            .httpBasic(org.springframework.security.config.Customizer.withDefaults()) // HTTP Basic para APIs
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/empleados", true)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/proyectos")
                .permitAll()
            );

        return http.build();
    }
}