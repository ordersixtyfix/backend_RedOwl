package com.beam.assetManagement.registration;


import com.beam.assetManagement.common.GenericResponse;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping(path="api/v1/registration")


public class RegistrationController {


    private RegistrationService registrationService;

    @PostMapping("/create")
    public GenericResponse<String> register(@RequestBody RegistrationRequest request){
        try{

            registrationService.register(request);
            return new GenericResponse<String>().setCode(200).setData("CREATED");
        }catch (Exception e){
            return new GenericResponse<String>().setCode(400).setData("CANNOT CREATED");
        }



    }
}
