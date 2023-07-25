package com.beam.assetManagement.assets;

import com.beam.assetManagement.assetRecon.AssetEnumeration;

import com.beam.assetManagement.assetRecon.IpData.IpData;
import com.beam.assetManagement.assetRecon.IpData.IpDataRepository;
import com.beam.assetManagement.assetRecon.IpData.SubdomainPortData;
import com.beam.assetManagement.assetRecon.ServiceEnum.ServiceEnum;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;


@RestController
@AllArgsConstructor
@RequestMapping(path="api/v1/asset")
public class AssetController {


    private AssetCreateService assetCreateService;

    private AssetEnumeration assetEnumeration;

    private AssetRepository assetRepository;

    private IpDataRepository ipDataRepository;

    private ServiceEnum serviceEnum;



    @PostMapping("/create")
    public String createAsset(@RequestBody AssetRequest request){

        return assetCreateService.createAsset(request);

    }

    @GetMapping("/create")
    public String createAsset(){


        return "TEST-CREATE-ENDPOINT-GET";

    }

    //SCAN SPECIFIC ASSET BY ASSET NAME
    @PostMapping("/scan/{name}")
    public ResponseEntity<Optional<Asset>> getAssetData(@PathVariable String name) throws Exception {
        Optional<Asset> asset = assetRepository.findByAssetName(name);

        assetEnumeration.setAsset(asset);

        return ResponseEntity.ok(asset);
    }

    @PostMapping("/get/{subdomain}")
    public ResponseEntity<List<SubdomainPortData>> getAssetScan(@PathVariable String subdomain) throws Exception {


        List<SubdomainPortData> subdomainPortData = assetEnumeration.getPortData(subdomain);
        if(subdomainPortData.isEmpty()){
            return new ResponseEntity(HttpStatus.SERVICE_UNAVAILABLE);
        }
        else{
            return ResponseEntity.ok(subdomainPortData);
        }
    }

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
        assetEnumeration.TryPortServiceAccess(ipData);
        return null;
    }




    @GetMapping("/get/asset-count")
    public String getAssetCount(){
        long number = assetRepository.count();

        return "{\"assetCount\":\"" + number + "\"}";

    }

    @GetMapping("/get/ip-count")
    public String getIpCount(){
        long number = ipDataRepository.count();

        return "{\"IpCount\":\"" + number + "\"}";

    }



    @PostMapping("test/{ipAddress}")
    public boolean mysqltest(@PathVariable String ipAddress){
        boolean accessData = serviceEnum.testMysql(ipAddress);
        return accessData;
    }






}
