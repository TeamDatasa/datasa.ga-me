package datasa.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
	
	private final JwtTokenProvider jwtTokenProvider;
	private final UserDetailsService userDetailsService;
	
	@Override
	protected void doFilterInternal(
			@NonNull HttpServletRequest request,
			@NonNull HttpServletResponse response,
			@NonNull FilterChain filterChain
	) throws ServletException, IOException {
		
		String path = request.getRequestURI();
		String method = request.getMethod();
		
		// 이미 인증이 있으면 패스
		if (SecurityContextHolder.getContext().getAuthentication() != null) {
			filterChain.doFilter(request, response);
			return;
		}
		
		String token = resolveToken(request);
		
		if (token == null || token.isBlank()) {
			log.debug("[JWT] no token: {} {}", method, path);
			filterChain.doFilter(request, response);
			return;
		}
		
		try {
			boolean valid = jwtTokenProvider.validate(token);
			if (!valid) {
				log.warn("[JWT] invalid token: {} {}", method, path);
				filterChain.doFilter(request, response);
				return;
			}
			
			String email = jwtTokenProvider.getEmail(token);
			if (email == null || email.isBlank()) {
				log.warn("[JWT] email empty: {} {}", method, path);
				filterChain.doFilter(request, response);
				return;
			}
			
			UserDetails userDetails = userDetailsService.loadUserByUsername(email);
			
			UsernamePasswordAuthenticationToken auth =
					new UsernamePasswordAuthenticationToken(
							userDetails,
							null,
							userDetails.getAuthorities()
					);
			
			auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
			SecurityContextHolder.getContext().setAuthentication(auth);
			
			log.info("[JWT] authenticated: email={}, {} {}", email, method, path);
			
		} catch (Exception e) {
			SecurityContextHolder.clearContext();
			log.error("[JWT] auth failed: {} {} / msg={}", method, path, e.getMessage(), e);
		}
		
		filterChain.doFilter(request, response);
	}
	
	private String resolveToken(HttpServletRequest request) {
		// 1) Authorization 헤더 우선
		String header = request.getHeader("Authorization");
		if (header != null && header.startsWith("Bearer ")) {
			return header.substring(7).trim();
		}
		
		// 2) 쿠키 access_token fallback
		Cookie[] cookies = request.getCookies();
		if (cookies == null) return null;
		
		for (Cookie c : cookies) {
			if ("access_token".equals(c.getName())) {
				String v = c.getValue();
				return v == null ? null : v.trim();
			}
		}
		return null;
	}
}
