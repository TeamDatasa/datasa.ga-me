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
								"/error",
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
						
						// ✅ HTML 상세 페이지(비로그인 허용)
						.requestMatchers(HttpMethod.GET, "/api/trip/detail/**").permitAll()
						
						// ✅ 여행 API 조회(비로그인 허용)
						.requestMatchers(HttpMethod.GET, "/api/trips/**").permitAll()
						
						// ✅ 댓글 목록 조회(비로그인 허용)
						.requestMatchers(HttpMethod.GET, "/api/trips/*/comments").permitAll()
						
						// 나머지는 인증 필요(댓글 작성/삭제 포함)
						.anyRequest().authenticated()
				)
				
				// ✅ 인증 안 된 요청은 401로
				.exceptionHandling(ex -> ex
						.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
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
