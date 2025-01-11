package com.example.LikeLink.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.LikeLink.Config.Security.Jwt.AuthEntryPointJwt;
import com.example.LikeLink.Config.Security.Jwt.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthEntryPointJwt unauthorizedHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        log.debug("Configuring SecurityFilterChain");
        
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.disable())  // Disable CORS for testing
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint(unauthorizedHandler)
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth
                // Public endpoints
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/test/**").permitAll()
                .requestMatchers("/api/health/**").permitAll()
                .requestMatchers("/error").permitAll()
                
                // Role-based authorization
                .requestMatchers("/api/patient/**").hasRole("PATIENT")
                .requestMatchers("/api/insurance/**").hasRole("PATIENT")
                .requestMatchers("/api/hospital/**").hasRole("HOSPITAL")
                .requestMatchers("/api/ambulance/**").hasRole("AMBULANCE_DRIVER")
                
                // Require authentication for all other requests
                .anyRequest().authenticated()
            )
            .authenticationProvider(authenticationProvider());

        // Add JWT filter
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        log.debug("SecurityFilterChain configuration completed");
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        
        log.debug("Configured DaoAuthenticationProvider with UserDetailsService and PasswordEncoder");
        return authProvider;
    }
}