package com.hospital.admision.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Habilita @PreAuthorize en los controladores
public class SecurityConfig {

    private static final String ADMIN = "ADMINISTRADOR";
    private static final String MEDICO = "MEDICO";
    private static final String RECEPCIONISTA = "RECEPCIONISTA";

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // El frontend consume la API con fetch desde el mismo origen
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Recursos públicos
                        .requestMatchers("/login.html", "/css/**", "/js/**", "/error").permitAll()

                        // Pantallas y API solo para ADMINISTRADOR
                        .requestMatchers("/usuarios.html", "/roles.html", "/auditoria.html").hasRole(ADMIN)
                        .requestMatchers("/api/usuarios/**", "/api/roles/**", "/api/auditoria/**").hasRole(ADMIN)

                        // Pacientes: todos consultan; ADMINISTRADOR y RECEPCIONISTA registran/editan/eliminan
                        .requestMatchers(HttpMethod.GET, "/api/pacientes/**").hasAnyRole(ADMIN, MEDICO, RECEPCIONISTA)
                        .requestMatchers("/api/pacientes/**").hasAnyRole(ADMIN, RECEPCIONISTA)

                        // Triaje y alta médica: ADMINISTRADOR y MEDICO
                        .requestMatchers("/api/admisiones/triaje", "/api/admisiones/alta").hasAnyRole(ADMIN, MEDICO)

                        // Admisiones (citas/ingresos): ADMINISTRADOR y RECEPCIONISTA
                        .requestMatchers(HttpMethod.POST, "/api/admisiones").hasAnyRole(ADMIN, RECEPCIONISTA)
                        .requestMatchers(HttpMethod.DELETE, "/api/admisiones/**").hasAnyRole(ADMIN, RECEPCIONISTA)

                        // Todo lo demás requiere haber iniciado sesión
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login.html")
                        .loginProcessingUrl("/login")
                        .successHandler(redireccionPorRol())
                        .failureHandler(fallaDeLogin())
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login.html?logout")
                        .permitAll()
                )
                .exceptionHandling(ex -> ex
                        // Sin sesión: la API responde 401 y las páginas redirigen al login
                        .authenticationEntryPoint((request, response, e) -> {
                            if (request.getRequestURI().startsWith("/api/")) {
                                escribirError(response, 401, "Debe iniciar sesión.");
                            } else {
                                response.sendRedirect("/login.html");
                            }
                        })
                        // Sin permiso: la API responde 403 y las páginas vuelven al panel principal
                        .accessDeniedHandler((request, response, e) -> {
                            if (request.getRequestURI().startsWith("/api/")) {
                                escribirError(response, 403, "No tiene permisos para realizar esta operación.");
                            } else {
                                response.sendRedirect("/index.html?denegado");
                            }
                        })
                );

        return http.build();
    }

    // Después del login, cada rol se envía a su módulo principal
    private AuthenticationSuccessHandler redireccionPorRol() {
        return (request, response, authentication) -> {
            if (tieneRol(authentication, ADMIN)) {
                response.sendRedirect("/usuarios.html");
            } else if (tieneRol(authentication, MEDICO)) {
                response.sendRedirect("/index.html#triaje");
            } else if (tieneRol(authentication, RECEPCIONISTA)) {
                response.sendRedirect("/index.html#paciente");
            } else {
                response.sendRedirect("/index.html");
            }
        };
    }

    // Distingue un usuario/rol desactivado de credenciales incorrectas
    private AuthenticationFailureHandler fallaDeLogin() {
        return (request, response, exception) -> {
            if (exception instanceof DisabledException) {
                response.sendRedirect("/login.html?inactivo");
            } else {
                response.sendRedirect("/login.html?error");
            }
        };
    }

    private boolean tieneRol(Authentication authentication, String rol) {
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + rol));
    }

    private void escribirError(jakarta.servlet.http.HttpServletResponse response, int status, String mensaje)
            throws java.io.IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"mensaje\":\"" + mensaje + "\"}");
    }
}
