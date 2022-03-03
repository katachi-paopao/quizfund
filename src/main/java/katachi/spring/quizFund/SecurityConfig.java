package katachi.spring.quizFund;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import katachi.spring.quizFund.service.OidcUserServiceImpl;
import katachi.spring.quizFund.service.UserDetailsServiceImpl;

@EnableWebSecurity
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Autowired
	UserDetailsServiceImpl userDetailsServiceImpl;

	@Autowired
	OidcUserServiceImpl oidcUserServiceImpl;

	@Autowired
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsServiceImpl)
				.passwordEncoder(passwordEncoder());
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/webjars/**", "/css/**", "/js/**", "/assets/**");
	}

	@Override
	protected void configure (HttpSecurity http) throws Exception {
		http
			.authorizeRequests()
				.antMatchers("/home", "/index", "/main", "/login", "/signup", "/auth/confirm/**", "/game/**", "/websocket/**").permitAll()
				.anyRequest().authenticated()
				.and()
			.formLogin()
				.loginPage("/login")
				.usernameParameter("user")
				.passwordParameter("password")
				.defaultSuccessUrl("/home")
				.and()
			.oauth2Login()
				.loginPage("/login")
				.userInfoEndpoint()
				.oidcUserService(oidcUserServiceImpl)
				.and()
				.defaultSuccessUrl("/home")
				.and()
			.logout()
				.logoutUrl("/logout")
				.logoutSuccessUrl("/home");
	}
}
