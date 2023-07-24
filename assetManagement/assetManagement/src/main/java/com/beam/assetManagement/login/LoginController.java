package com.beam.assetManagement.login;

import com.beam.assetManagement.registration.RegistrationRequest;
import com.beam.assetManagement.user.User;
import io.jsonwebtoken.Jwt;
import jakarta.servlet.http.Cookie;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.ParameterResolutionDelegate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;


@RestController
@AllArgsConstructor
@RequestMapping(path="api/v1")
public class LoginController {

    private LoginService loginService;
    @Autowired
    private AuthenticationManager authentication;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;


    @PostMapping("/login")
    public String login(@RequestBody LoginRequest authRequest, HttpServletResponse response) {

        Authentication authObject;
        try {
            authentication.authenticate(new
                    UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));


        } catch (BadCredentialsException e) {
            return null;
        }

        String token = jwtService.generateToken(authRequest.getEmail());


        return "{\"token\":\"" + token + "\"}";

    }

    @PostMapping("/validate")
    public String validate(@RequestBody TokenValidationRequest request) {


        String token = request.getToken();

        return token;
    }
}
