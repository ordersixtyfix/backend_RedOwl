package com.beam.assetManagement.login;

import com.beam.assetManagement.security.validator.EmailValidator;
import com.beam.assetManagement.user.User;
import com.beam.assetManagement.user.UserRepository;
import com.beam.assetManagement.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class LoginService {


    private UserService userService;

    private UserRepository userRepository;

    private EmailValidator emailValidator;

    private BCryptPasswordEncoder bCryptPasswordEncoder;







    public String login(LoginRequest request) {

        String password = request.getPassword();
        String email = request.getEmail();

        BCryptPasswordEncoder b = new BCryptPasswordEncoder();

        Optional<User> user = userRepository.findByEmail(email);

        String hashedpassword = null;
        if (user.isPresent()) {
            User existingCustomer = user.get();
            hashedpassword = existingCustomer.getPassword();
        }




        if(b.matches(password, hashedpassword)){
            return "ACCESS";
        }
        else {
            return "ACCESS DENIED";
        }


    }





}
