package com.beam.assetManagement.firm;

import com.beam.assetManagement.common.GenericResponse;
import com.beam.assetManagement.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/firm")
public class FirmController {

    private final FirmService firmService;

    private final UserService userService;

    @PostMapping()
    public GenericResponse setFirm(@RequestBody FirmRequest firmRequest) {
        try {
            firmService.createFirm(firmRequest);

            return new GenericResponse().setCode(200);
        } catch (Exception e) {
            return new GenericResponse().setCode(400);
        }
    }


    @PostMapping("/{userId}/{firmId}")
    public GenericResponse deleteFirm(@PathVariable String firmId, @PathVariable String userId) {
        try {
            firmService.deleteFirm(firmId, userId);
            return new GenericResponse().setCode(200);
        } catch (Exception e) {
            return new GenericResponse().setCode(400);
        }
    }

    @GetMapping("{userId}")
    public GenericResponse<List<Firm>> getAllFirms(@PathVariable String userId) {
        try {

            String role = userService.isSuperUser(userId);
            if (role == "SUPER_USER") {
                List<Firm> firmList = firmService.getAllFirms();
                return new GenericResponse<List<Firm>>().setCode(200).setData(firmList);
            } else {
                return new GenericResponse().setCode(400);
            }


        } catch (Exception e) {
            return new GenericResponse().setCode(400);
        }
    }


    @GetMapping("firm-name/{firmId}")
    public GenericResponse<String> getFirmName(@PathVariable String firmId) {


        try {
            String firmName = firmService.getFirmName(firmId);
            return new GenericResponse<String>().setCode(200).setData(firmName);


        } catch (Exception e) {
            return new GenericResponse().setCode(400);
        }

    }

}
