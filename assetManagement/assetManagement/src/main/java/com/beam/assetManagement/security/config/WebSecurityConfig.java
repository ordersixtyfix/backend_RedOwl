package com.beam.assetManagement.security.config;

import com.beam.assetManagement.login.LoginSuccessHandler;
import com.beam.assetManagement.security.filter.JwtAuthFilter;
import com.beam.assetManagement.user.AppUserRole;
import com.beam.assetManagement.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

import static com.beam.assetManagement.user.AppUserRole.SUPER_USER;
import static com.beam.assetManagement.user.AppUserRole.USER;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@AllArgsConstructor
@EnableWebSecurity
public class WebSecurityConfig{

    private final UserService userService;

    private final LoginSuccessHandler loginSuccessHandler;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private JwtAuthFilter authFilter;
    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userService);
        provider.setPasswordEncoder(bCryptPasswordEncoder);
        return provider;
    }

    @Bean
    AuthenticationManager getAuthenticationManager(AuthenticationConfiguration configuration) throws Exception{
        return configuration.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
        configuration.setAllowedHeaders(Arrays.asList("Content-Type", "Authorization"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        configuration.setAllowCredentials(true);
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    protected SecurityFilterChain configure(HttpSecurity http) throws Exception {




        return http

                .csrf(csrf->csrf.disable())
                .cors(withDefaults())
                .authorizeHttpRequests(auth -> {

                    auth.requestMatchers(AntPathRequestMatcher.antMatcher("/api/v1/registration")).permitAll();
                    auth.requestMatchers(AntPathRequestMatcher.antMatcher("/api/v1/login")).permitAll();
                    auth.requestMatchers(AntPathRequestMatcher.antMatcher("/api/v1/asset/create")).permitAll();
                    auth.requestMatchers(AntPathRequestMatcher.antMatcher("/api/v1/asset/get/**")).authenticated();
                    auth.requestMatchers(AntPathRequestMatcher.antMatcher("/api/v1/asset/scan/**")).permitAll();
                    auth.requestMatchers(AntPathRequestMatcher.antMatcher("/login/**")).permitAll();
                    auth.requestMatchers(AntPathRequestMatcher.antMatcher("/api/v1/asset/access/ports/**")).permitAll();
                    auth.requestMatchers(AntPathRequestMatcher.antMatcher("/private/**")).authenticated();
                    auth.requestMatchers(AntPathRequestMatcher.antMatcher("/home/**")).hasAnyRole(String.valueOf(SUPER_USER),String.valueOf(USER));


                    auth.anyRequest().authenticated();




                })

                .csrf(csrf -> csrf.ignoringRequestMatchers(AntPathRequestMatcher.antMatcher("/api/v*/registration/**")))
                .csrf(csrf -> csrf.ignoringRequestMatchers(AntPathRequestMatcher.antMatcher("/api/v1/asset/create")))
                .csrf(csrf -> csrf.ignoringRequestMatchers(AntPathRequestMatcher.antMatcher("/api/v1/asset/get/**")))
                .csrf(csrf -> csrf.ignoringRequestMatchers(AntPathRequestMatcher.antMatcher("/api/v1/asset/scan/**")))
                .csrf(csrf -> csrf.ignoringRequestMatchers(AntPathRequestMatcher.antMatcher("/api/v1/login")))
                .csrf(csrf -> csrf.ignoringRequestMatchers(AntPathRequestMatcher.antMatcher("/login")))
                .csrf(csrf -> csrf.ignoringRequestMatchers(AntPathRequestMatcher.antMatcher("/api/v1/asset/access/ports/**")))


                .sessionManagement(httpSecuritySessionManagementConfigurer -> httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)




                .formLogin(form->form

                        .successHandler(loginSuccessHandler)

                        .permitAll()

                )



                .build();












    }














}
