package datasa.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
	
	private final JwtTokenProvider jwtTokenProvider;
	private final UserDetailsService userDetailsService;
	
	@Override
	protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
		String path = request.getServletPath();
		
		return path.equals("/reset-password")
				|| path.equals("/error")
				|| path.equals("/")
				|| path.equals("/index")
				|| path.equals("/login")
				|| path.equals("/signup")
				|| path.startsWith("/css/")
				|| path.startsWith("/js/")
				|| path.startsWith("/images/")
				|| path.startsWith("/webjars/")
				|| path.equals("/api/auth/login")
				|| path.equals("/api/auth/signup")
				|| path.startsWith("/api/auth/password/")
				|| path.startsWith("/api/auth/email/")
				|| path.startsWith("/api/notifications/");
	}
	
	@Override
	protected void doFilterInternal(
			@NonNull HttpServletRequest request,
			@NonNull HttpServletResponse response,
			@NonNull FilterChain filterChain
	) throws ServletException, IOException {
		
		String token = resolveToken(request);
		
		try {
			// 토큰 없으면 익명으로 통과
			if (token == null || token.isBlank()) {
				filterChain.doFilter(request, response);
				return;
			}
			
			// 이미 인증이 세팅되어 있으면 중복 세팅하지 않음
			if (SecurityContextHolder.getContext().getAuthentication() == null
					&& jwtTokenProvider.validate(token)) {
				
				String email = jwtTokenProvider.getEmail(token);
				
				UserDetails userDetails = userDetailsService.loadUserByUsername(email);
				
				UsernamePasswordAuthenticationToken auth =
						new UsernamePasswordAuthenticationToken(
								userDetails,
								null,
								userDetails.getAuthorities()
						);
				
				SecurityContextHolder.getContext().setAuthentication(auth);
			}
			
		} catch (Exception e) {
			// 토큰 만료/서명 오류 등 -> 500 금지, 익명 처리
			SecurityContextHolder.clearContext();
		}
		
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
