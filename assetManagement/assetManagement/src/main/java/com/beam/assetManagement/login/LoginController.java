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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@AllArgsConstructor
public class LoginController {

    private LoginService loginService;
    @Autowired
    private AuthenticationManager authentication;

    @Autowired
    private JwtService jwtService;



    @PostMapping("/api/v1/login")
    public void login(@RequestBody LoginRequest authRequest,HttpServletResponse response) throws Exception {

        Authentication authObject;
        try{
            authentication.authenticate(new
                    UsernamePasswordAuthenticationToken(authRequest.getEmail(),authRequest.getPassword()));
            //SecurityContextHolder.getContext().setAuthentication(authObject);

        }catch (BadCredentialsException e){
            throw new UsernameNotFoundException("invalid user request");
        }

        String token = jwtService.generateToken(authRequest.getEmail());
        Cookie cookie = new Cookie("token", token);
        cookie.setHttpOnly(true);
        //return new ResponseEntity<HttpStatus>(HttpStatus.OK);


        response.addCookie(cookie);
        response.setStatus(HttpServletResponse.SC_OK);
    }


}
