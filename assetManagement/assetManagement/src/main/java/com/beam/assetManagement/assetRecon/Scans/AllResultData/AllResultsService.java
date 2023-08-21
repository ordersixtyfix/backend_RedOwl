package com.beam.assetManagement.assetRecon.Scans.AllResultData;


import com.beam.assetManagement.assetRecon.IpData.IpData;
import com.beam.assetManagement.assetRecon.IpData.IpDataRepository;
import com.beam.assetManagement.assetRecon.Scans.FtpData.FtpService;
import com.beam.assetManagement.assetRecon.Scans.MySqlData.MySqlService;
import com.beam.assetManagement.assetRecon.Scans.PostgreSqlData.PostGreService;
import com.beam.assetManagement.assetRecon.Scans.ScanRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class AllResultsService {
    private final FtpService ftpService;
    private final MySqlService mySqlService;
    private final PostGreService postGreService;
    private final IpDataRepository ipDataRepository;

    public Object allScanner(String assetId, String firmId, ScanRequest scanType) throws IOException,ClassNotFoundException {
        List<String> scanTypesList = scanType.getScanType();
        List<Object> results = new ArrayList<>();
        List<IpData> ipDataList = ipDataRepository.findByAssetId(assetId);
        for (String type : scanTypesList) {
            DataBaseType dataBaseType = DataBaseType.valueOf(type.toUpperCase());

            switch (dataBaseType) {

                case FTP -> {
                    for (IpData ipData : ipDataList) {
                        String ipAddress = ipData.getIpAddress();
                        Object ftpReport = ftpService.ftpResult(assetId,firmId,ipAddress);
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
                case ALL -> {

                    for (IpData ipData : ipDataList) {
                        String ipAddress = ipData.getIpAddress();

                        Object ftpReportAll = ftpService.ftpResult(assetId, firmId, ipAddress);
                        results.add(ftpReportAll);

                        Object postgreReportAll = postGreService.testPostgreSQL(assetId, firmId, ipAddress);
                        results.add(postgreReportAll);

                        Object mySqlReportAll = mySqlService.testMysql(assetId, firmId, ipAddress);
                        results.add(mySqlReportAll);
                    }
                    return results;
                }
            }
        }
        return results;
    }
}
