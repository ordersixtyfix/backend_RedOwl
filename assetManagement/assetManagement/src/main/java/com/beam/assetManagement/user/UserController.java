package com.beam.assetManagement.user;


import com.beam.assetManagement.common.GenericResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RequestMapping("api/v1")
@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping("user/{userId}")
    public GenericResponse validateSuperUser(@PathVariable String userId) {


        String isAdmin = userService.isSuperUser(userId);
        if (Objects.equals(isAdmin, "SUPER_USER")) {
            return new GenericResponse().setCode(200);
        } else {
            return new GenericResponse().setCode(400);
        }
    }

    @GetMapping("user-by-firm/{userId}/{firmId}")
    public GenericResponse<UserDto> getFirstUserByFirmId(@PathVariable String firmId, @PathVariable String userId){


        try{
            String isAdmin = userService.isSuperUser(userId);

            if (Objects.equals(isAdmin, "SUPER_USER")) {
                UserDto userDto =userService.getFirstUserByFirmId(firmId);
                return new GenericResponse<UserDto>().setCode(200).setData(userDto);
            } else {
                return new GenericResponse().setCode(400);
            }



        }
        catch (Exception e){
            return new GenericResponse<UserDto>().setCode(400);
        }



    }





}
