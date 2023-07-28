package com.beam.assetManagement.registration;

import com.beam.assetManagement.security.validator.EmailValidator;
import com.beam.assetManagement.user.AppUserRole;
import com.beam.assetManagement.user.User;
import com.beam.assetManagement.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final UserService userService;
    private final EmailValidator emailValidator;




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
