package com.kombaos.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http,
                                            @Value("${kombaos.security.enabled:false}") boolean securityEnabled) throws Exception {
        http.csrf(csrf -> csrf.disable());

        if (!securityEnabled) {
            http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
            return http.build();
        }

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/health", "/actuator/health", "/api/public/**").permitAll()
                .requestMatchers("/api/auth/login").authenticated()
                .requestMatchers("/api/materials/**", "/api/inventory/**", "/api/production/**").hasAnyRole("ADMIN", "OPERACION")
                .requestMatchers("/api/products/**", "/api/sales/**").hasAnyRole("ADMIN", "COMERCIAL")
                .requestMatchers("/api/traceability/**").hasAnyRole("ADMIN", "OPERACION", "COMERCIAL")
                .requestMatchers("/api/**").hasRole("ADMIN")
                .anyRequest().permitAll());

        http.httpBasic(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    UserDetailsService userDetailsService(PasswordEncoder passwordEncoder,
                                          @Value("${kombaos.security.users.admin.username:admin}") String adminUser,
                                          @Value("${kombaos.security.users.admin.password:admin123}") String adminPassword,
                                          @Value("${kombaos.security.users.operacion.username:operacion}") String operacionUser,
                                          @Value("${kombaos.security.users.operacion.password:operacion123}") String operacionPassword,
                                          @Value("${kombaos.security.users.comercial.username:comercial}") String comercialUser,
                                          @Value("${kombaos.security.users.comercial.password:comercial123}") String comercialPassword) {

        return new InMemoryUserDetailsManager(
                User.withUsername(adminUser)
                        .password(passwordEncoder.encode(adminPassword))
                        .roles("ADMIN")
                        .build(),
                User.withUsername(operacionUser)
                        .password(passwordEncoder.encode(operacionPassword))
                        .roles("OPERACION")
                        .build(),
                User.withUsername(comercialUser)
                        .password(passwordEncoder.encode(comercialPassword))
                        .roles("COMERCIAL")
                        .build()
        );
    }
}
