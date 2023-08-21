package com.beam.assetManagement.assets;

import com.beam.assetManagement.assetRecon.AssetEnumerationService;
import com.beam.assetManagement.assetRecon.IpData.IpData;
import com.beam.assetManagement.assetRecon.IpData.IpDataService;
import com.beam.assetManagement.assetRecon.Scans.AllResultData.AllResultsService;
import com.beam.assetManagement.assetRecon.Scans.FtpData.FtpReport;
import com.beam.assetManagement.assetRecon.Scans.FtpData.FtpService;
import com.beam.assetManagement.assetRecon.Scans.MySqlData.MySqlService;
import com.beam.assetManagement.assetRecon.Scans.PostgreSqlData.PostGreService;
import com.beam.assetManagement.assetRecon.Scans.PostgreSqlData.PostGreSqlReport;
import com.beam.assetManagement.assetRecon.Scans.ScanRequest;
import com.beam.assetManagement.common.GenericResponse;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
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
    private final FtpService ftpService;
    private final IpDataService ipDataService;
    private final MySqlService mySqlService;

    private final PostGreService postGreService;

    private final AssetService assetService;
    private final AllResultsService allResultsService;

    @PostMapping("/create")
    public GenericResponse<String> createAsset(@RequestBody AssetRequest request) {

        try {
            assetService.createAsset(request);

            return new GenericResponse<String>().setCode(200);
        } catch (Exception e) {
            return new GenericResponse<String>().setCode(400);
        }

    }

    @PostMapping("/shodan/{IpAddress}")
    public GenericResponse<String> ShodanEnum(@PathVariable String IpAddress) {

        try {
            ipDataService.shodanIpEnum(IpAddress);

            return new GenericResponse<String>().setCode(200);
        } catch (Exception e) {
            return new GenericResponse<String>().setCode(400);
        }

    }



    @PostMapping("/create-admin/{userId}")
    public GenericResponse<String> createAssetBySuperUser(@RequestBody AssetRequest request, @PathVariable String userId) {

        try {
            boolean isAdmin = assetService.userValidation(userId);
            if (isAdmin) {

                assetService.createAssetBySuperUser(request);
                return new GenericResponse<String>().setCode(200);
            } else {
                return new GenericResponse<String>().setCode(400);
            }


        } catch (Exception e) {
            return new GenericResponse<String>().setCode(400);
        }

    }


    //SCAN SPECIFIC ASSET BY ASSET NAME
    @PostMapping("/scan/{assetId}/{firmId}")
    public GenericResponse<Optional<Asset>> getAssetData(@PathVariable String assetId, @PathVariable String firmId) throws Exception {
        try {
            assetEnumerationService.setAsset(assetId,firmId);
            return new GenericResponse<Optional<Asset>>().setCode(200);
        } catch (Exception e) {
            return new GenericResponse<Optional<Asset>>().setCode(400);
        }


    }




    @GetMapping("/get/scandata/{assetId}")
    public GenericResponse<List<IpData>> getScanData(@PathVariable String assetId) throws Exception {

        try {
            List<IpData> ipData = ipDataService.getIpDataObjectList(assetId);

            return new GenericResponse<List<IpData>>().setCode(200).setData(ipData);

        } catch (Exception e) {
            return new GenericResponse<List<IpData>>().setCode(400);
        }


    }


    //GET ALL ASSET DATA
    @GetMapping("/get/assetdata/{userId}/{firmId}")
    public GenericResponse<Page<Asset>> getAsset(@RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "2") int size,
                                                 @PathVariable String userId,@PathVariable String firmId) {


        try {
            boolean isAdmin = assetService.userValidation(userId);
            if (isAdmin) {
                return new GenericResponse<Page<Asset>>().setCode(200).setData(assetService.getAllAssets(page, size));
            } else {
                return new GenericResponse<Page<Asset>>().setCode(200).setData(assetService.getAssetByFirmId(firmId, page, size));
            }


        } catch (Exception e) {
            return new GenericResponse<Page<Asset>>().setCode(400);
        }

    }

    //TEST ENDPOINT
    @PostMapping("/access/ports/{assetId}")
    public ResponseEntity<List<IpData>> scanAccessiblePortService(@PathVariable String assetId) throws IOException {
        List<IpData> ipData = ipDataService.getIpDataObjectList(assetId);
        assetEnumerationService.TryPortServiceAccess(ipData);
        return null;
    }


    @GetMapping("/get/asset-count/{userId}/{firmId}")
    public GenericResponse<Long> getAssetCount(@PathVariable String firmId, @PathVariable String userId) {
        try {
            return new GenericResponse<Long>().setCode(200).setData(assetService.getAssetCount(firmId,userId));
        } catch (Exception e) {
            return new GenericResponse<Long>().setCode(400);
        }


    }

    @GetMapping("/get/ip-count/{firmId}")
    public GenericResponse<Long> getIpCount(@PathVariable String firmId) {
        try {

            return new GenericResponse<Long>().setCode(200).setData(assetService.getIpCount(firmId));
        } catch (Exception e) {
            return new GenericResponse<Long>().setCode(400);
        }
    }





    @GetMapping("get/{assetId}")
    public GenericResponse<String> getAssetName(@PathVariable String assetId) {
        try {
            return new GenericResponse<String>().setCode(200).setData(assetService.getAssetById(assetId).
                    get().getAssetName());

        } catch (Exception e) {
            return new GenericResponse<String>().setCode(400);
        }


    }





    @PostMapping("/scanselect/{assetId}/{firmId}")
    public GenericResponse<?> scanResultGenericResponse(
            @PathVariable String assetId, @PathVariable String firmId,
            @RequestBody ScanRequest scanType
    ) {
        try {
            Object allResults = allResultsService.allScanner(assetId, firmId, scanType);
            return new GenericResponse<>().setCode(200).setData(allResults);
        } catch (Exception e) {
            e.printStackTrace();
            return new GenericResponse<>().setCode(522);
        }
    }

    @GetMapping("/get/ftp/{assetId}")
    public GenericResponse<List<FtpReport>> getAllFtpReports(@PathVariable String assetId) {
        try {
            return new GenericResponse<List<FtpReport>>().setCode(200).setData(ftpService.findByAssetId(assetId));
        } catch (Exception e) {
            return new GenericResponse<List<FtpReport>>().setCode(400);
        }
    }

    @GetMapping("/get/postgresql/{assetId}")
    public GenericResponse<List<PostGreSqlReport>> getAllPostgresqlReports(@PathVariable String assetId) {
        try {
            return new GenericResponse<List<PostGreSqlReport>>().setCode(200).setData(postGreService.findByAssetId(assetId));
        } catch (Exception e) {
            return new GenericResponse<List<PostGreSqlReport>>().setCode(400);
        }
    }


}
