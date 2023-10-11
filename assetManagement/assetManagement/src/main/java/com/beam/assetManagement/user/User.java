package com.beam.assetManagement.user;

import com.beam.assetManagement.assetRecon.Base.Base;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document(collection = "Users")
@TypeAlias("User")
@SuperBuilder

public class User extends Base {


    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private AppUserRole appUserRole;
    private String firmId;


}
