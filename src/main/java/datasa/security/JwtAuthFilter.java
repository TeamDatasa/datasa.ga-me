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
		
		String token = null;
		
		// 1) Authorization 헤더 우선
		String header = request.getHeader("Authorization");
		if (header != null && header.startsWith("Bearer ")) {
			token = header.substring(7);
		}
		
		// 2) 없으면 쿠키에서 access_token 찾기
		if (token == null && request.getCookies() != null) {
			for (Cookie c : request.getCookies()) {
				if ("access_token".equals(c.getName())) {
					token = c.getValue();
					break;
				}
			}
		}
		
		// ✅ 3) 토큰 검증 + 인증 세팅
		if (token != null && jwtTokenProvider.validate(token)
				&& SecurityContextHolder.getContext().getAuthentication() == null) {
			
			String email = jwtTokenProvider.getEmail(token);
			UserDetails userDetails = userDetailsService.loadUserByUsername(email);
			
			UsernamePasswordAuthenticationToken auth =
					new UsernamePasswordAuthenticationToken(
							userDetails, null, userDetails.getAuthorities()
					);
			
			SecurityContextHolder.getContext().setAuthentication(auth);
		}
		
		// ✅ 4) 다음 필터로 넘기기 (이거 없으면 요청이 멈춤)
		filterChain.doFilter(request, response);
	}
}
