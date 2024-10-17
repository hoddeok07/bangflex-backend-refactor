package com.swcamp9th.bangflixbackend.config;

//import com.swcamp9th.bangflixbackend.security.config.CorsConfig;
import com.swcamp9th.bangflixbackend.security.jwt.JwtAuthorizationFilter;
import com.swcamp9th.bangflixbackend.common.util.JwtUtil;
import com.swcamp9th.bangflixbackend.security.user.UserDetailsServiceImpl;
import jakarta.servlet.Filter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

	private final JwtUtil jwtUtil;
	private final UserDetailsServiceImpl userDetailsService;
	private final AuthenticationConfiguration authenticationConfiguration;
//	private final CorsConfig corsConfig;


	public WebSecurityConfig(
			JwtUtil jwtUtil,
			UserDetailsServiceImpl userDetailsService,
			AuthenticationConfiguration authenticationConfiguration
//			, CorsConfig corsConfig
	) {
		this.jwtUtil = jwtUtil;
		this.userDetailsService = userDetailsService;
		this.authenticationConfiguration = authenticationConfiguration;
//		this.corsConfig = corsConfig;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}

	@Bean
	public Filter jwtAuthorizationFilter() {
		return new JwtAuthorizationFilter(jwtUtil, userDetailsService);
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(AbstractHttpConfigurer::disable);

//		http.cors(corsConfig -> corsConfig.getClass());

		http.formLogin(AbstractHttpConfigurer::disable);

		http.sessionManagement(sessionManagement ->
			sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
		);

		http.authorizeHttpRequests(authorize ->
			authorize
				.requestMatchers("/api/v1/auth/**").permitAll()
				.requestMatchers("/swagger-ui/**", "/v3/**").permitAll()
				.anyRequest().authenticated()
		);

		http.addFilterBefore(jwtAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}
}