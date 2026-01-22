package datasa.security;

import datasa.domain.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {
	private final Key key;
	private final long validityMs;

	public JwtTokenProvider(
			@Value("${jwt.secret}") String secret,
			@Value("${jwt.validity-ms:3600000}") long validityMs
	) {
		this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
		this.validityMs = validityMs;
	}

	public String createToken(String email, User.Role role) {
		Date now = new Date();
		Date exp = new Date(now.getTime() + validityMs);

		return Jwts.builder()
				.setSubject(email)
				.claim("role", role.name())
				.setIssuedAt(now)
				.setExpiration(exp)
				.signWith(key, SignatureAlgorithm.HS256)
				.compact();
	}

	public boolean validate(String token) {
		try {
			Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
			return true;
		} catch (JwtException | IllegalArgumentException e) {
			return false;
		}
	}

	public String getEmail(String token) {
		return getClaims(token).getSubject();
	}

	public String getRole(String token) {
		Object role = getClaims(token).get("role");
		return role == null ? null : role.toString();
	}

	private Claims getClaims(String token) {
		return Jwts.parserBuilder().setSigningKey(key).build()
				.parseClaimsJws(token)
				.getBody();
	}
}
