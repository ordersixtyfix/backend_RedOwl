package com.beam.assetManagement.registration;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor


public class RegistrationRequest {

    private String firstName;
    private String lastName;
    private String password;
    private String email;
    private String firmId;


}
