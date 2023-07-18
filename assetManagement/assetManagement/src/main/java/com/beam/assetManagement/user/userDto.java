package com.beam.assetManagement.user;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.UUID;
@Data
public class userDto {

    @Id
    private String userId;

    private String firstName;

    private String lastName;

    private String email;

    private String password;

    private AppUserRole appUserRole;







    public userDto(String userId,
                String lastName,
                String firstName,
                String email,
                String password,
                AppUserRole appUserRole
    ){
        this.userId = userId;
        this.lastName=lastName;
        this.firstName=firstName;
        this.email=email;
        this.password=password;
        this.appUserRole=appUserRole;



    }


}
