package com.beam.assetManagement.user;

import com.beam.assetManagement.assetRecon.Base.Base;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class UserDto extends Base{


    private String firstName;

    private String lastName;
    private String email;
    private AppUserRole appUserRole;

    private String jwtToken;

    private String firmId;


}
