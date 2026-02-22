package datasa.config;

import datasa.security.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST,
                                "/api/auth/login",
                                "/api/auth/signup",
                                "/api/auth/logout",
                                "/api/auth/password/reset",
                                "/api/auth/password/reset-request",
                                "/api/auth/email/send-code",
                                "/api/auth/email/verify-code"
                        ).permitAll()
                        .requestMatchers(HttpMethod.GET,
                                "/api/auth/me"
                        ).permitAll()
                        // ===== 정적/공개 =====
                        .requestMatchers(
                                "/",
                                "/index",
                                "/listAll",
                                "/detail/**",
                                "/error",
                                "/community",
                                "/trip/listAll",
                                "/favicon.ico",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/webjars/**",
                                "/login",
                                "/signup",
                                "/auth/**",
                                "/api/auth/**"
                        ).permitAll()
                        .requestMatchers(HttpMethod.GET, "/reset-password**").permitAll()

                        .requestMatchers(HttpMethod.GET, "/trip/detail/**").permitAll()

                        .requestMatchers(HttpMethod.GET, "/trips/**").permitAll()

                        .requestMatchers(HttpMethod.GET, "/trips/*/comments").permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/mypage/host/card").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/api/mypage/host/card").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/hosts/*/card").permitAll()

                        .requestMatchers(HttpMethod.DELETE, "/api/auth/withdraw").authenticated()

                        .requestMatchers(HttpMethod.GET, "/api/trips/**").permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/trips/*/comments").permitAll()


                        // 나머지는 인증 필요(댓글 작성/삭제 포함)
                        .anyRequest().authenticated()
                )

                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                )

                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                      .headers(headers -> headers
                .frameOptions(frame -> frame.sameOrigin())
        );

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
