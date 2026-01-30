package datasa.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
	
	private final JwtTokenProvider jwtTokenProvider;
	
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
				|| path.startsWith("/api/auth/");
	}
	
	@Override
	protected void doFilterInternal(
			@NonNull HttpServletRequest request,
			@NonNull HttpServletResponse response,
			@NonNull FilterChain filterChain
	) throws ServletException, IOException {
		
		System.out.println("JWT FILTER HIT: " + request.getMethod() + " " + request.getRequestURI());
		
		String token = resolveToken(request);
		
		// 토큰이 아예 없으면: 그냥 통과
		if (token == null || token.isBlank()) {
			filterChain.doFilter(request, response);
			return;
		}
		
		// 토큰이 있는데 유효하지 않으면: 401로 종료
		if (!jwtTokenProvider.validateToken(token)) {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}
		
		// 유효하면 SecurityContext 세팅 후 통과
		Authentication auth = jwtTokenProvider.getAuthentication(token);
		SecurityContextHolder.getContext().setAuthentication(auth);
		
		filterChain.doFilter(request, response);
	}
	
	private String resolveToken(HttpServletRequest request) {
		// 1) Authorization 헤더
		String header = request.getHeader("Authorization");
		if (header != null && header.startsWith("Bearer ")) {
			return header.substring(7);
		}
		
		// 2) 쿠키 access_token
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie c : cookies) {
				if ("access_token".equals(c.getName())) {
					return c.getValue();
				}
			}
		}
		return null;
	}
}
