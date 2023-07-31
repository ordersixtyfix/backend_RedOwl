package com.beam.assetManagement.login;

import com.beam.assetManagement.common.GenericResponse;
import com.beam.assetManagement.user.UserDto;
import com.beam.assetManagement.user.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping(path="api/v1")
public class LoginController {




    private final AuthenticationManager authentication;


    private final JwtService jwtService;

    private final UserService userService;





   /* @PostMapping("/login")
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
*/


    @PostMapping("/login")
    public GenericResponse<UserDto> login(@RequestBody LoginRequest authRequest, HttpServletResponse response) {

        Authentication authObject;
        try {
            authentication.authenticate(new
                    UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));


        } catch (BadCredentialsException e) {
            return new GenericResponse<UserDto>().setCode(400);
        }

        UserDto userDto = userService.getUserByEmail(authRequest.getEmail());

        String token = jwtService.generateToken(authRequest.getEmail());

        userDto.setJwtToken(token);


        return new GenericResponse<UserDto>().setCode(200).setData(userDto);

    }


}
