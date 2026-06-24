package com.wut.dormrepair.config;

import com.wut.dormrepair.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        // PUBLIC
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/auth/logout").permitAll()

                        // STUDENT
                        .requestMatchers(HttpMethod.POST, "/api/repair/submit").hasRole("STUDENT")
                        .requestMatchers(HttpMethod.GET, "/api/repair/user/**").hasAnyRole("STUDENT", "REPAIR_STAFF", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/repair/{orderId}").hasAnyRole("STUDENT", "REPAIR_STAFF", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/repair/my-orders").hasRole("STUDENT")
                        .requestMatchers(HttpMethod.PUT, "/api/repair/*").hasAnyRole("STUDENT", "REPAIR_STAFF", "ADMIN")

                        // NEW: Student view their assignment
                        .requestMatchers(HttpMethod.GET, "/api/repair/my-assignment").hasRole("STUDENT")

                        // STAFF
                        .requestMatchers(HttpMethod.POST, "/api/repair/status").hasAnyRole("REPAIR_STAFF","ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/repair/list").hasAnyRole("REPAIR_STAFF","ADMIN")

                        // NEW: Staff view their assignments
                        .requestMatchers(HttpMethod.GET, "/api/repair/staff-assignments").hasRole("REPAIR_STAFF")
                        .requestMatchers(HttpMethod.GET, "/api/repair/assigned-to-me").hasRole("REPAIR_STAFF")

                        // ADMIN DORMITORY MANAGEMENT
                        .requestMatchers(HttpMethod.POST, "/api/admin/dormitory/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/admin/dormitory/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/admin/dormitory/**").hasRole("ADMIN")

                        // NEW: ADMIN STAFF MANAGEMENT
                        .requestMatchers(HttpMethod.GET, "/api/admin/staff").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/admin/staff").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/admin/staff/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/admin/staff/**").hasRole("ADMIN")

                        // NEW: ADMIN ASSIGNMENT MANAGEMENT
                        .requestMatchers("/api/admin/assignments/**").hasRole("ADMIN")

                        // OTHER ADMIN ENDPOINTS
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/admin/report/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/repair/*").hasRole("ADMIN")

                        // PUBLIC DORMITORY LISTING
                        .requestMatchers(HttpMethod.GET, "/api/dormitories").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/dormitories/**").authenticated()

                        .anyRequest().authenticated()
                );

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With"));
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}