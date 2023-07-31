package com.beam.assetManagement.registration;

import com.beam.assetManagement.user.AppUserRole;
import com.beam.assetManagement.user.User;
import com.beam.assetManagement.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final UserService userService;





    public User register(RegistrationRequest request) {
        try {

            if (request.getFirstName().isEmpty() ||
                    request.getLastName().isEmpty()||
                    request.getPassword().isEmpty() ||
                    request.getEmail().isEmpty()) {
                throw new IllegalArgumentException("All fields in the RegistrationRequest must be provided.");
            }




            return userService.signUpUser(User.builder()
                    .lastName(request.getLastName())
                    .firstName(request.getFirstName())
                    .email(request.getEmail())
                    .password(request.getPassword())
                    .appUserRole(AppUserRole.USER).build());



        } catch (Exception e) {

            throw new IllegalStateException("User cannot created");
        }
    }
    }


