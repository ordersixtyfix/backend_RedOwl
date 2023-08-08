package com.beam.assetManagement.Scans.AllResultData;

import com.beam.assetManagement.Scans.FtpData.FtpService;
import com.beam.assetManagement.Scans.MySqlData.MySqlService;
import com.beam.assetManagement.Scans.PostgreSqlData.PostGreService;
import com.beam.assetManagement.common.GenericResponse;
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
    private final AllResults allResults;
//    public Object allScannerTest(String ipAddress,DataBaseType dataBaseType, Object result) throws IOException, SQLException, ClassNotFoundException {
//
//        switch (dataBaseType) {
//            case FTP:
//                FtpReport ftpReport = ftpService.ftpScanTest(ipAddress);
//                result=ftpReport;
//                return result;
//
//            case POSTGRESQL:
//                PostGreSqlReport postgreReport = postGreService.testPostgreSQL(ipAddress).getData();
//                result=postgreReport;
//                return result;
//            case MYSQL:
//                Object mySqlReport = mySqlService.testMysql(ipAddress);
//                result=mySqlReport;
//                return result;
//            case ALL:
//                FtpReport ftpReportAll = ftpService.ftpScanTest(ipAddress);
//                PostGreSqlReport postgreReportAll = postGreService.testPostgreSQL(ipAddress).getData();
//                Object mySqlReportAll = mySqlService.testMysql(ipAddress);
//                allResults.setFtpReport(ftpReportAll);
//                allResults.setPostGreSqlReport(postgreReportAll);
//                allResults.setMySqlReport((MySqlReport) mySqlReportAll);
//                result=allResults;
//                return result;
//            default:
//                return null;
//        }
//    }

    public Object allScanner(String assetId,String scanType) throws IOException,ClassNotFoundException {
        String[] scanTypes = scanType.split(",");
        List<Object> results = new ArrayList<>();
        Object result = null;
        for (String type : scanTypes) {
            DataBaseType dataBaseType = DataBaseType.valueOf(type.toUpperCase());
            switch (dataBaseType) {
                case FTP -> {
                    Object ftpReport = ftpService.ftpResult(assetId);
                    results.add(ftpReport);
                }
                case POSTGRESQL -> {
                    Object postgreReport = postGreService.testPostgreSQL(assetId);
                    results.add(postgreReport);
                }
                case MYSQL -> {
                    Object mySqlReport = mySqlService.testMysql(assetId);
                    results.add(mySqlReport);
                }
                case ALL -> {
                    AllResults allResults = new AllResults();

                    Object ftpReportAll = ftpService.ftpResult(assetId);
                    allResults.setFtpResult((GenericResponse<?>) ftpReportAll);

                    Object postgreReportAll = postGreService.testPostgreSQL(assetId);
                    allResults.setPostGreSqlResult((GenericResponse<?>) postgreReportAll);

                    Object mySqlReportAll = mySqlService.testMysql(assetId);
                    allResults.setMySqlResult((GenericResponse<?>) mySqlReportAll);

                    result = allResults;
                    return result;
                }
            }
    }
        return results;
    }
}
