package com.workshop.badr.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private JwtAthFilter jwtAthFilter;
  private static final List<UserDetails> APPLICATION_USERS = Arrays.asList(
    new User(
      "badrvkacimi@gmail.com",
      "password",
      Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
    ),
    new User(
      "testbadr@gmail.com",
      "password",
      Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
    )
  );
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests()
        .anyRequest()
        .authenticated()
        .and()
        .authenticationProvider(authenticationProvider())
        .addFilterBefore(jwtAthFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }
  @Bean
  public AuthenticationProvider authenticationProvider() {
       final DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
       daoAuthenticationProvider.setUserDetailsService(userDetailsService());
       daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
       return daoAuthenticationProvider;
  }

  @Bean
  private PasswordEncoder passwordEncoder() {
    //return new BCryptPasswordEncoder();
    return NoOpPasswordEncoder.getInstance();
  }

  @Bean
  public UserDetailsService userDetailsService(){
    return email -> APPLICATION_USERS
      .stream()
      .filter(u-> u.getUsername().equals(email))
      .findFirst()
      .orElseThrow(()-> new UsernameNotFoundException("No user was found"));
  }

}
