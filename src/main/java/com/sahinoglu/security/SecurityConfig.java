package com.sahinoglu.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		http.csrf(csrf -> csrf.disable())

				.authorizeHttpRequests(auth -> auth

						// Public endpoints
						.requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**", "/login").permitAll()

						// Organization administration
						.requestMatchers("/api/v1/admin/centers/**").hasRole("ORG_ADMIN")

						.requestMatchers("/api/v1/admin/branches/**").hasAnyRole("ORG_ADMIN", "CENTER_ADMIN")

						.requestMatchers("/api/v1/admin/employees/**").hasAnyRole("ORG_ADMIN", "CENTER_ADMIN")

						.requestMatchers("/api/v1/admin/coins/**").hasRole("ORG_ADMIN")

						.requestMatchers(HttpMethod.GET, "/api/v1/admin/transactions")
						.hasAnyRole("ORG_ADMIN", "CENTER_ADMIN", "BRANCH_ADMIN")

						// Transaction requests
						.requestMatchers(HttpMethod.POST, "/api/v1/transaction-requests").hasRole("BRANCH_OPERATOR")

						.requestMatchers(HttpMethod.PATCH, "/api/v1/transaction-requests/*/approve",
								"/api/v1/transaction-requests/*/reject")
						.hasRole("CENTER_OPERATOR")

						// Wallet operations
						// These must be declared before the general /wallets/** rule.
						.requestMatchers(HttpMethod.POST, "/api/v1/wallets/*/deposit").hasRole("BRANCH_OPERATOR")

						.requestMatchers(HttpMethod.POST, "/api/v1/wallets/*/withdraw").hasRole("BRANCH_OPERATOR")

						.requestMatchers(HttpMethod.POST, "/api/v1/wallets/*/convert").hasRole("BRANCH_OPERATOR")

						.requestMatchers("/api/v1/admin/wallets/**")
						.hasAnyRole("ORG_ADMIN", "CENTER_ADMIN", "BRANCH_ADMIN")

						// General authenticated endpoints
						.requestMatchers("/api/v1/coins/**", "/api/v1/centers/**", "/api/v1/branches/**",
								"/api/v1/wallets/**")
						.authenticated()

						.anyRequest().authenticated())

				.formLogin(login -> login.loginPage("/login").defaultSuccessUrl("/login", true).permitAll())
				.logout(logout -> logout.logoutUrl("/logout").logoutSuccessUrl("/login"));

		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}