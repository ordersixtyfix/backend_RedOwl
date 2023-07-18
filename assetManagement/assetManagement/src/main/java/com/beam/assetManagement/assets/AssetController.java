package com.beam.assetManagement.assets;

import com.beam.assetManagement.assetRecon.AssetEnumeration;

import com.beam.assetManagement.assetRecon.IpData;
import com.beam.assetManagement.assetRecon.IpDataRepository;
import com.beam.assetManagement.assetRecon.SubdomainPortData;
import lombok.AllArgsConstructor;
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



    @PostMapping("/create")
    public String createAsset(@RequestBody AssetRequest request){

        return assetCreateService.createAsset(request);

    }

    @GetMapping("/create")
    public String createAsset(){


        return "TEST-CREATE-ENDPOINT-GET";

    }


    @PostMapping("/get/{name}")
    public ResponseEntity<Optional<Asset>> getAssetData(@PathVariable String name) throws Exception {
        Optional<Asset> asset = assetRepository.findByAssetName(name);

        assetEnumeration.setAsset(asset);

        return ResponseEntity.ok(asset);
    }

    @PostMapping("/scan/{subdomain}")
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

    @GetMapping("/get/assetdata/")
    public ResponseEntity<List<Asset>> getAsset() throws Exception {
        List<Asset> asset = assetRepository.findAll();
        return ResponseEntity.ok(asset);
    }


    @PostMapping("/access/ports/{assetId}")
    public ResponseEntity<List<IpData>> scanAccessiblePortService(@PathVariable String assetId) throws IOException {
        List<IpData> ipData = ipDataRepository.findByAssetId(assetId);
        assetEnumeration.TryPortServiceAccess(ipData);


        return null;


    }






}
