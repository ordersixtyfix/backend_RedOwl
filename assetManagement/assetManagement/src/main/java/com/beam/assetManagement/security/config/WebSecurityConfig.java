package com.beam.assetManagement.security.config;

import com.beam.assetManagement.user.UserDto;
import com.beam.assetManagement.user.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.switchuser.SwitchUserFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@AllArgsConstructor
@EnableWebSecurity
public class WebSecurityConfig {

    private final ObjectMapper objectMapper;
    private final UserService userService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userService);
        provider.setPasswordEncoder(bCryptPasswordEncoder);
        return provider;
    }

    @Bean
    public SwitchUserFilter switchUserFilter() {
        SwitchUserFilter filter = new SwitchUserFilter();
        filter.setUserDetailsService(userService);
        filter.setSuccessHandler(authenticationSuccessHandler());
        filter.setFailureHandler(authenticationFailureHandler());
        return filter;
    }

    @Bean
    AuthenticationManager getAuthenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Content-Type", "Authorization"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        configuration.setAllowCredentials(true);
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    protected SecurityFilterChain configure(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())

                .cors(withDefaults())
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers(AntPathRequestMatcher.antMatcher("/api/v1/registration")).permitAll();
                    auth.requestMatchers(AntPathRequestMatcher.antMatcher("/api/v1/asset/**")).authenticated();
                    auth.requestMatchers(AntPathRequestMatcher.antMatcher("/private/**")).authenticated();
                    auth.requestMatchers(AntPathRequestMatcher.antMatcher("/api/v1/statics/**")).authenticated();
                    auth.requestMatchers(AntPathRequestMatcher.antMatcher("/api/v1/user/**")).authenticated();
                    auth.requestMatchers(AntPathRequestMatcher.antMatcher("/api/v1/switch-user")).authenticated();
                    auth.requestMatchers(AntPathRequestMatcher.antMatcher("/api/v1/firm/**")).authenticated();
                    auth.requestMatchers(AntPathRequestMatcher.antMatcher("/upload/**")).authenticated();

                    auth.anyRequest().authenticated();
                })
                //.csrf(csrf -> csrf.ignoringRequestMatchers(AntPathRequestMatcher.antMatcher("/api/v1/**")))
                .formLogin(form -> form
                        .successHandler(authenticationSuccessHandler())
                        .failureHandler(authenticationFailureHandler())


                )
                .addFilterAfter(switchUserFilter(), FilterSecurityInterceptor.class)



                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .authenticationProvider(authenticationProvider())

                .build();
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {


        return new SimpleUrlAuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

                UserDto userDto = userService.getUserByEmail(authentication.getName());


                Map<String, Object> jsonResponse = new HashMap<>();
                response.setContentType("application/json");
                jsonResponse.put("statusCode", HttpServletResponse.SC_OK);
                jsonResponse.put("user", userDto);


                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.writeValue(response.getWriter(), jsonResponse);
            }
        };
    }

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return new SimpleUrlAuthenticationFailureHandler() {
            @Override
            public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {


                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.setContentType("application/json");


                Map<String, Object> jsonResponse = new HashMap<>();
                jsonResponse.put("statusCode", HttpServletResponse.SC_BAD_REQUEST);


                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.writeValue(response.getWriter(), jsonResponse);
            }
        };
    }

}
