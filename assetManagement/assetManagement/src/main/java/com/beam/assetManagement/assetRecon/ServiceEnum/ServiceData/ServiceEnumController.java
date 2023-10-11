package com.beam.assetManagement.assetRecon.ServiceEnum.ServiceData;

import com.beam.assetManagement.assetRecon.ServiceEnum.MySqlData.MySqlReport;
import com.beam.assetManagement.assetRecon.ServiceEnum.MySqlData.MySqlService;
import com.beam.assetManagement.common.GenericResponse;
import com.beam.assetManagement.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "api/v1/service-report")
@Slf4j
public class ServiceEnumController {

    private final MySqlService mySqlService;

    private final UserService userService;


    @GetMapping("/mysql/{userId}/{assetId}")
    public GenericResponse<List<MySqlReport>> getMysqlReport(@PathVariable String userId, @PathVariable String assetId) throws Exception {
        try {
            String firmId = userService.getFirmIdByUserId(userId);
            List<MySqlReport> mySqlReport = mySqlService.findByAssetIdAndFirmId(assetId,firmId);
            return new GenericResponse<List<MySqlReport>>().setCode(200).setData(mySqlReport);
        } catch (Exception e) {
            return new GenericResponse<List<MySqlReport>>().setCode(400);
        }
    }


}
