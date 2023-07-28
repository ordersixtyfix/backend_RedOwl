package com.beam.assetManagement.assets;

import com.beam.assetManagement.assetRecon.AssetEnumerationService;
import com.beam.assetManagement.assetRecon.IpData.IpData;
import com.beam.assetManagement.assetRecon.IpData.IpDataRepository;
import com.beam.assetManagement.assetRecon.ServiceEnum.ServiceEnum;
import com.beam.assetManagement.common.GenericResponse;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;


@RestController
@AllArgsConstructor
@RequestMapping(path = "api/v1/asset")
public class AssetController {

    private final AssetEnumerationService assetEnumerationService;

    private final AssetRepository assetRepository;

    private final IpDataRepository ipDataRepository;

    private final ServiceEnum serviceEnum;
    private final AssetService assetService;


    @PostMapping("/create")
    public GenericResponse<String> createAsset(@RequestBody AssetRequest request) {

        boolean isCreated = assetService.createAsset(request);

        return new GenericResponse<String>().setCode((isCreated ? 200 : 400));
    }


    //SCAN SPECIFIC ASSET BY ASSET NAME
    @PostMapping("/scan/{assetId}")
    public GenericResponse<Optional<Asset>> getAssetData(@PathVariable String assetId) throws Exception {
        try {
            assetEnumerationService.setAsset(assetId);
            return new GenericResponse<Optional<Asset>>().setCode(200);
        } catch (Exception e) {
            return new GenericResponse<Optional<Asset>>().setCode(400);
        }


    }

    /*
        @PostMapping("/get/{subdomain}")
        public ResponseEntity<List<SubdomainPortData>> getAssetScan(@PathVariable String subdomain) throws Exception {


            List<SubdomainPortData> subdomainPortData = assetEnumerationService.getPortData(subdomain);
            if(subdomainPortData.isEmpty()){
                return new ResponseEntity(HttpStatus.SERVICE_UNAVAILABLE);
            }
            else{
                return ResponseEntity.ok(subdomainPortData);
            }
        }
    */
    @GetMapping("/get/scandata/{assetId}")
    public GenericResponse<List<IpData>> getScanData(@PathVariable String assetId) throws Exception {

        try {
            List<IpData> ipData = ipDataRepository.findByAssetId(assetId);

            return new GenericResponse<List<IpData>>().setCode(200).setData(ipData);

        } catch (Exception e) {
            return new GenericResponse<List<IpData>>().setCode(400);
        }


    }


    //GET ALL ASSET DATA
    @GetMapping("/get/assetdata/")
    public GenericResponse<Page<Asset>> getAsset(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "2") int size) {
        try {
            PageRequest pageRequest = PageRequest.of(page, size);
            Page<Asset> assets = assetRepository.findAll(pageRequest);
            return new GenericResponse<Page<Asset>>().setCode(200).setData(assets);

        }catch (Exception e){
            return new GenericResponse<Page<Asset>>().setCode(400);
        }

    }

    //TEST ENDPOINT
    @PostMapping("/access/ports/{assetId}")
    public ResponseEntity<List<IpData>> scanAccessiblePortService(@PathVariable String assetId) throws IOException {
        List<IpData> ipData = ipDataRepository.findByAssetId(assetId);
        assetEnumerationService.TryPortServiceAccess(ipData);
        return null;
    }


    @GetMapping("/get/asset-count")
    public GenericResponse<Long> getAssetCount() {
        try{
            return new GenericResponse<Long>().setCode(200).setData(assetService.getAssetCount());
        }
        catch (Exception e){
            return new GenericResponse<Long>().setCode(400);
        }


    }

    @GetMapping("/get/ip-count")
    public GenericResponse<Long> getIpCount() {
        try{
            return new GenericResponse<Long>().setCode(200).setData(assetService.getIpCount());
        }catch (Exception e){
            return new GenericResponse<Long>().setCode(400);
        }


    }


    //TEST ENDPOINT
    @PostMapping("test/{ipAddress}")
    public boolean mysqltest(@PathVariable String ipAddress) {
        boolean accessData = serviceEnum.testPostgreSQL(ipAddress);
        return accessData;
    }


    @GetMapping("get/{assetId}")
    public GenericResponse<String> getAssetName(@PathVariable String assetId){
         try{
             return new GenericResponse<String>().setCode(200).setData(assetService.getAssetName(assetId).
                     get().getAssetName());

         }catch (Exception e){
             return new GenericResponse<String>().setCode(400);
         }


    }


}
