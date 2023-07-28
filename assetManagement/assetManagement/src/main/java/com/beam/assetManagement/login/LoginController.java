package com.beam.assetManagement.login;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@AllArgsConstructor
@RequestMapping(path="api/v1")
public class LoginController {


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


}
