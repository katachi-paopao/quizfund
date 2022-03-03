package katachi.spring.quizFund.model.user;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

public class GoogleOAuth2User implements UserDetails, MyUserDetails, OidcUser {
	private User user;
	private OidcUser oidcUser;
	private Collection<GrantedAuthority> authorities;

	public GoogleOAuth2User(User user, OidcUser oidcUser) {
		this.user = user;
		this.oidcUser = oidcUser;
		this.authorities = new ArrayList<>();
	}

	@Override
	public Map<String, Object> getAttributes() {
		return this.oidcUser.getAttributes();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return this.authorities;
	}

	@Override
	public String getName() {
		return this.user.getMail();
	}

	@Override
	public Map<String, Object> getClaims() {
		return this.oidcUser.getClaims();
	}

	@Override
	public OidcUserInfo getUserInfo() {
		return this.oidcUser.getUserInfo();
	}

	@Override
	public OidcIdToken getIdToken() {
		return this.oidcUser.getIdToken();
	}

	@Override
	public String getPassword() {
		return this.user.getPassword();
	}

	@Override
	public String getUsername() {
		return this.user.getMail();
	}

	@Override
	public String getUserId() {
		return this.user.getUuid();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

}
