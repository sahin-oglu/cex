package com.sahinoglu.security;

import org.springframework.context.annotation.Bean;
//ayni path'i kullanan farkli request'leri tefrik etmek icin.
import org.springframework.http.HttpMethod;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		http.csrf(csrf -> csrf.disable()).authorizeHttpRequests(auth -> auth

				// swagger'i whitelist'e almak gerekiyor..
				.requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()

				//

				.requestMatchers("/login").permitAll()

				// org_admin'in tum center'lar uzerinde otoritesi vardir.
				.requestMatchers("/api/v1/admin/centers/**").hasRole("ORG_ADMIN")
				.requestMatchers("/api/v1/admin/branches/**").hasAnyRole("ORG_ADMIN", "CENTER_ADMIN")
				.requestMatchers("/api/v1/admin/employees/**").hasAnyRole("ORG_ADMIN", "CENTER_ADMIN")
				.requestMatchers("/api/v1/admin/coins/**").hasRole("ORG_ADMIN")

				
				
				.requestMatchers(HttpMethod.POST, "/api/v1/transaction-requests").hasRole("BRANCH_OPERATOR")
				.requestMatchers(HttpMethod.PATCH, "/api/v1/transaction-requests/*/approve").hasRole("CENTER_OPERATOR")
				.requestMatchers(HttpMethod.PATCH, "/api/v1/transaction-requests/*/reject").hasRole("CENTER_OPERATOR")

//				.requestMatchers("/admin/ui/**").authenticated()

				.requestMatchers(HttpMethod.GET, "/api/v1/wallets/*/assets").authenticated()
				.requestMatchers("/api/v1/coins/**").authenticated()
				.requestMatchers("/api/v1/centers/**", "/api/v1/branches/**", "/api/v1/wallets/**").authenticated()

				
				
				.anyRequest().authenticated())

				.formLogin(
						login -> login.loginPage("/login").defaultSuccessUrl("/admin/ui/dashboard", true).permitAll())

				.logout(logout -> logout.logoutUrl("/logout").logoutSuccessUrl("/login"));

		return http.build();
	}

	@Bean
	@SuppressWarnings("deprecation")
	public static NoOpPasswordEncoder passwordEncoder() {
		return (NoOpPasswordEncoder) NoOpPasswordEncoder.getInstance();
	}
}