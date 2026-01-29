package datasa.config;

import datasa.security.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
                .headers(headers -> headers
                        .frameOptions(frame -> frame.disable())
                )
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
				.requestMatchers(
					"/", "/index", "/favicon.ico",
					"/css/**", "/js/**", "/images/**", "/webjars/**",
					"/error", "/reset-password",
					"/login", "/signup",
					"/auth/**"
					).permitAll()
					
                .requestMatchers(
                    "/api/auth/password/**",
                    "/api/map/naver",
                    "/api/map/places",
                    "/api/map/kakao",
                    "/api/trip/**",
					"/api/auth/email/**",
					"/api/trip/update/**",
                    "/mypage",
                    "/mypage/details",
                    "/api/map/main",
                    "/trip",
                    "/chat/**",
                    "/api/trips/**",
                    "/host/**",
					"/css/**", "/js/**", "/images/**",
					"/favicon.ico",
                    "/host/**","/ws/**","/api/chat/**"
                ).permitAll()
                .requestMatchers("/api/auth/**").permitAll()
				.requestMatchers("/mypage/**", "/api/mypage/**").authenticated()
                .requestMatchers(
                    "/api/application/**",
                    "/api/chat/**"
                ).authenticated()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}