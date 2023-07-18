package com.beam.assetManagement.registration;

import com.beam.assetManagement.user.UserService;
import com.beam.assetManagement.user.AppUserRole;
import com.beam.assetManagement.registration.RegistrationRequest;
import com.beam.assetManagement.user.User;
import com.beam.assetManagement.user.UserRepository;
import com.beam.assetManagement.security.validator.EmailValidator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RegistrationService {

    private final UserService userService;
    private final EmailValidator emailValidator;

    private final UserRepository userRepository;



    public User register(RegistrationRequest request) {

        boolean isValidEmail = emailValidator.test(request.getEmail());

        if(!isValidEmail){
            throw new IllegalStateException("email not valid");
        }
        return userService.signUpUser(



                new User(

                        request.getLastName(),
                        request.getFirstName(),
                        request.getEmail(),
                        request.getPassword(),
                        AppUserRole.USER

                )





        );
    }

}
