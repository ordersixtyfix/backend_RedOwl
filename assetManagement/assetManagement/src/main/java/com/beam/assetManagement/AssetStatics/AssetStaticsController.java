package com.beam.assetManagement.AssetStatics;

import com.beam.assetManagement.common.GenericResponse;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping(path = "api/v1/statics")
public class AssetStaticsController {

    private AssetStaticsService assetStaticsService;


    @GetMapping("/port-statics/{userId}/{firmId}")
    public GenericResponse<AssetPortStatics> getPortStatics(@PathVariable String firmId,@PathVariable String userId) {

        try {
            return new GenericResponse<AssetPortStatics>().setCode(200)
                    .setData(assetStaticsService.getPortStatics(firmId,userId));
        } catch (Exception e) {
            return new GenericResponse<AssetPortStatics>().setCode(400);
        }
    }

    @GetMapping("/port-service-statics/{userId}/{firmId}")
    public GenericResponse<AssetPortServiceStatics> getPortServiceStatics(@PathVariable String userId,
                                                                          @PathVariable String firmId){

        try{
            return new GenericResponse<AssetPortServiceStatics>().setCode(200)
                    .setData(assetStaticsService.getAssetPortServiceStatics(userId,firmId));
        }catch (Exception e){
            return new GenericResponse<AssetPortServiceStatics>().setCode(400);
        }
    }


    @GetMapping("/total-dns-servers/{userId}/{firmId}")
    public GenericResponse<Long> getDnsServerCount(@PathVariable String userId,
                                                   @PathVariable String firmId){
        try{
            return new GenericResponse<Long>().setCode(200).setData(assetStaticsService.getDnsServerCount(userId, firmId));
        }catch (Exception e){
            return new GenericResponse<Long>().setCode(400);
        }



    }

    @GetMapping("/total-subdomains/{userId}/{firmId}")
    public GenericResponse<Long> getSubdomainCount(@PathVariable String userId,
                                                   @PathVariable String firmId){
        try{
            return new GenericResponse<Long>().setCode(200).setData(assetStaticsService.getSubdomainCount(userId, firmId));
        }catch (Exception e){
            return new GenericResponse<Long>().setCode(400);
        }



    }







}
