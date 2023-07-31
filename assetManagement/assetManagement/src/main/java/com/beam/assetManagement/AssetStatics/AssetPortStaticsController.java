package com.beam.assetManagement.AssetStatics;

import com.beam.assetManagement.common.GenericResponse;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping(path = "api/v1/statics")
public class AssetPortStaticsController {

    private AssetPortStaticsService assetPortStaticsService;



    @GetMapping("/all-port-statics")
    public GenericResponse<AssetPortStatics> getPortStatics() {

        try {
            return new GenericResponse<AssetPortStatics>().setCode(200).setData(assetPortStaticsService.
                    getAllPortStatics());
        } catch (Exception e) {
            return new GenericResponse<AssetPortStatics>().setCode(400);
        }
    }
}
