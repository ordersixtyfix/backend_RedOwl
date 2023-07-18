package com.beam.assetManagement.user;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

@Data

@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "Users")

public class User {




    @Id
    private String userId;

    private String firstName;

    private String lastName;

    private String email;

    private String password;

    private AppUserRole appUserRole;







    public User(String lastName,
                String firstName,
                String email,
                String password,
                AppUserRole appUserRole
                ){
        this.userId = UUID.randomUUID().toString();
        this.lastName=lastName;
        this.firstName=firstName;
        this.email=email;
        this.password=password;
        this.appUserRole=appUserRole;



    }




}
