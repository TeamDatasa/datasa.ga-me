package datasa.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
	
	private final JwtTokenProvider jwtTokenProvider;
	private final CustomUserDetailsService userDetailsService;
	
	@Override
	protected void doFilterInternal(
			HttpServletRequest request,
			HttpServletResponse response,
			FilterChain filterChain
	) throws ServletException, IOException {
		
		String header = request.getHeader("Authorization");
		String token = null;
		
		if (header != null && header.startsWith("Bearer ")) {
			token = header.substring(7);
		}
		
		if (token != null && jwtTokenProvider.validate(token)) {
			String email = jwtTokenProvider.getEmail(token);
			
			UserDetails userDetails = userDetailsService.loadUserByUsername(email);
			
			var auth = new UsernamePasswordAuthenticationToken(
					userDetails, null, userDetails.getAuthorities()
			);
			
			SecurityContextHolder.getContext().setAuthentication(auth);
		}
		
		filterChain.doFilter(request, response);
	}
}
