package datasa.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
	
	private final JwtTokenProvider jwtTokenProvider;
	private final UserDetailsService userDetailsService; // CustomUserDetailsService가 빈이면 이것으로도 주입됨
	
	@Override
	protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
		String path = request.getServletPath();
		return path.equals("/reset-password")
				|| path.equals("/error")
				|| path.equals("/")
				|| path.equals("/index")
				|| path.startsWith("/css/")
				|| path.startsWith("/js/")
				|| path.startsWith("/images/")
				|| path.startsWith("/webjars/")
				|| path.startsWith("/api/auth/"); // 로그인/회원가입/비번재설정 API들
	}
	
	@Override
	protected void doFilterInternal(
			@NonNull HttpServletRequest request,
			@NonNull HttpServletResponse response,
			@NonNull FilterChain filterChain
	) throws ServletException, IOException {
		
		System.out.println("JWT FILTER HIT: " + request.getMethod() + " " + request.getRequestURI());
		
		String token = null;
		
		// 1) Authorization 헤더 우선
		String header = request.getHeader("Authorization");
		if (header != null && header.startsWith("Bearer ")) {
			token = header.substring(7);
		}
		
		// 2) 없으면 쿠키에서 access_token 찾기
		if (token == null && request.getCookies() != null) {
			for (var c : request.getCookies()) {
				if ("access_token".equals(c.getName())) {
					token = c.getValue();
					break;
				}
			}
		}
		
	}
}