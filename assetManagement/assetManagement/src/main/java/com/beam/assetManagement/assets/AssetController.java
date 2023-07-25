package com.beam.assetManagement.assets;

import com.beam.assetManagement.assetRecon.AssetEnumerationService;
import com.beam.assetManagement.assetRecon.IpData.IpData;
import com.beam.assetManagement.assetRecon.IpData.IpDataRepository;
import com.beam.assetManagement.assetRecon.ServiceEnum.ServiceEnum;
import com.beam.assetManagement.common.GenericResponse;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;


@RestController
@AllArgsConstructor
@RequestMapping(path="api/v1/asset")
public class AssetController {



    @Autowired
    private AssetEnumerationService assetEnumerationService;
    @Autowired
    private AssetRepository assetRepository;

    private IpDataRepository ipDataRepository;

    private ServiceEnum serviceEnum;



    private AssetService assetService;

    @Autowired
    public AssetController(@Lazy AssetService assetService){
        this.assetService = assetService;
    }



    @PostMapping("/create")
    public String createAsset(@RequestBody AssetRequest request){

        return assetService.createAsset(request);

    }

    @GetMapping("/create")
    public String createAsset(){


        return "TEST-CREATE-ENDPOINT-GET";

    }

    //SCAN SPECIFIC ASSET BY ASSET NAME
    @PostMapping("/scan/{name}")
    public ResponseEntity<Optional<Asset>> getAssetData(@PathVariable String name) throws Exception {
        Optional<Asset> asset = assetRepository.findByAssetName(name);

        assetEnumerationService.setAsset(asset);

        return ResponseEntity.ok(asset);
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
    @PostMapping("/get/scandata/{assetId}")
    public ResponseEntity<List<IpData>> getScanData(@PathVariable String assetId) throws Exception {
        List<IpData> ipData = ipDataRepository.findByAssetId(assetId);
        return ResponseEntity.ok(ipData);
    }


    //GET ALL ASSET DATA
    @GetMapping("/get/assetdata/")
    public ResponseEntity<Page<Asset>> getAsset(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "2") int size
    ) {
        // Create a Pageable object to represent pagination
        PageRequest pageRequest = PageRequest.of(page, size);

        // Fetch the paginated asset data from the repository
        Page<Asset> assets = assetRepository.findAll(pageRequest);

        return ResponseEntity.ok(assets);
    }


    @PostMapping("/access/ports/{assetId}")
    public ResponseEntity<List<IpData>> scanAccessiblePortService(@PathVariable String assetId) throws IOException {
        List<IpData> ipData = ipDataRepository.findByAssetId(assetId);
        assetEnumerationService.TryPortServiceAccess(ipData);
        return null;
    }




    @GetMapping("/get/asset-count")
    public GenericResponse<Long> getAssetCount(){
        return new GenericResponse<Long>().setCode(200).setData(assetService.getAssetCount());

    }

    @GetMapping("/get/ip-count")
    public GenericResponse<Long> getIpCount(){
        return new GenericResponse<Long>().setCode(200).setData(assetService.getIpCount());
    }



    @PostMapping("test/{ipAddress}")
    public boolean mysqltest(@PathVariable String ipAddress){
        boolean accessData = serviceEnum.testPostgreSQL(ipAddress);
        return accessData;
    }






}
