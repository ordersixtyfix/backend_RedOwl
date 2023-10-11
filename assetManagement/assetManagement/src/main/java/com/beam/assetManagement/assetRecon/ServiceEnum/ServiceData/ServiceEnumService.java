package com.beam.assetManagement.assetRecon.ServiceEnum.ServiceData;


import com.beam.assetManagement.assetRecon.IpData.IpData;
import com.beam.assetManagement.assetRecon.IpData.IpDataRepository;
import com.beam.assetManagement.assetRecon.ServiceEnum.FtpData.FtpService;
import com.beam.assetManagement.assetRecon.ServiceEnum.MySqlData.MySqlService;
import com.beam.assetManagement.assetRecon.ServiceEnum.PostgreSqlData.PostGreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ServiceEnumService {
    private final FtpService ftpService;
    private final MySqlService mySqlService;
    private final PostGreService postGreService;
    private final IpDataRepository ipDataRepository;

    public Object allScanner(String assetId, String firmId, List<String> scanTypesList, String userId) throws IOException,ClassNotFoundException {
        List<Object> results = new ArrayList<>();
        List<IpData> ipDataList = ipDataRepository.findByAssetId(assetId);

        for (String type : scanTypesList) {
            ServiceType serviceType = ServiceType.valueOf(type.toUpperCase());

            switch (serviceType) {

                case FTP -> {
                    for (IpData ipData : ipDataList) {
                        String ipAddress = ipData.getIpAddress();
                        Object ftpReport = ftpService.ftpResult(assetId,firmId,ipAddress,userId);
                        results.add(ftpReport);
                    }
                }
                case POSTGRESQL -> {

                    for (IpData ipData : ipDataList) {
                        String ipAddress = ipData.getIpAddress();
                        Object postgreReport = postGreService.testPostgreSQL(assetId, firmId,ipAddress);
                        results.add(postgreReport);
                    }
                }
                case MYSQL -> {

                    for (IpData ipData : ipDataList) {
                        String ipAddress = ipData.getIpAddress();
                        Object mySqlReport = mySqlService.testMysql(assetId,firmId,ipAddress);
                        results.add(mySqlReport); 
                    }
                }
            }
        }
        return results;
    }
}
