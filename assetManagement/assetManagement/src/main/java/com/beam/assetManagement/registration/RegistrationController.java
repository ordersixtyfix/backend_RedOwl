package com.beam.assetManagement.registration;



import com.beam.assetManagement.user.User;
import com.beam.assetManagement.user.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping(path="api/v1/registration")


public class RegistrationController {


    private RegistrationService registrationService;

    @PostMapping
    public User register(@RequestBody RegistrationRequest request){

        User user =registrationService.register(request);
        return user;

    }
}
