package com.beam.assetManagement.assetRecon.ServiceEnum;


import com.beam.assetManagement.Scans.MySqlData.MySqlService;
import com.beam.assetManagement.Scans.PostgreSqlData.PostGreService;
import com.beam.assetManagement.assetRecon.IpData.IpData;
import com.beam.assetManagement.assetRecon.IpData.IpDataRepository;
import com.beam.assetManagement.assetRecon.IpData.SubdomainPortData;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;


@Service
@AllArgsConstructor
public class ServiceEnum {

    private final IpDataRepository ipDataRepository;
    private final MySqlService mySqlService;
    private final PostGreService postGreService;
    public void findAssets(String assetId) throws IOException, SQLException, ClassNotFoundException {
        List<IpData> ipDataList = ipDataRepository.findByAssetId(assetId);

        for(IpData obj: ipDataList){
            List<SubdomainPortData> subdomainPortDataList = obj.getPortScanData();
            String ipAddress = obj.getIpAddress();
            for(SubdomainPortData obj2 : subdomainPortDataList){
                String service = obj2.getPortService();
                switch (service) {
                    case "mysql" -> mySqlService.testMysql(ipAddress);
                    case "postgresql" -> postGreService.testPostgreSQL(ipAddress);
                    case "mongodb" -> System.out.println("mongodb");
                    case "ftp" -> System.out.println("ftp");
                    default -> System.out.println("there is no service for scanning");
                }
            }

            }
    }

    public boolean testConnection(String service,String ipAddress) throws IOException, SQLException, ClassNotFoundException {

    switch (service){
        case "mysql":
            mySqlService.testMysql(ipAddress);
            break;

        case "postgresql":
            postGreService.testPostgreSQL(ipAddress);
            break;

        case "mongodb":
            System.out.println("mongodb");
            break;
        case "ftp":
            System.out.println("mongodb");
            break;
    }
        return false;
    }
}






