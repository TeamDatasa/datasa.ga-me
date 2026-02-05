package datasa.security;

import lombok.*;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Builder
@Getter
@AllArgsConstructor
public class CustomUserDetail implements UserDetails {
	private Long userId;
	private String userEmail;
	private String passwordHash;
	private final Collection<? extends GrantedAuthority> authorities;
	
	
	public Long getUserId() {
		return userId;
	}
	
	@Override
	public String getUsername() {
		return userEmail;
	}
	
	@Override
	public @Nullable String getPassword() {
		return passwordHash;
	}
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}
	


	
	@Override
	public boolean isAccountNonExpired() {
		return UserDetails.super.isAccountNonExpired();
	}
	
	@Override
	public boolean isAccountNonLocked() {
		return UserDetails.super.isAccountNonLocked();
	}
	
	@Override
	public boolean isCredentialsNonExpired() {
		return UserDetails.super.isCredentialsNonExpired();
	}
	
	@Override
	public boolean isEnabled() {
		return UserDetails.super.isEnabled();
	}
}
