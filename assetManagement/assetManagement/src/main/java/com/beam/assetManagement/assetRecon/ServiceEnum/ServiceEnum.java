package com.beam.assetManagement.assetRecon.ServiceEnum;


import com.beam.assetManagement.assetRecon.IpData.IpData;
import com.beam.assetManagement.assetRecon.IpData.IpDataRepository;
import com.beam.assetManagement.assetRecon.IpData.SubdomainPortData;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.List;


@Service
@AllArgsConstructor
public class ServiceEnum {


    private final IpDataRepository ipDataRepository;



    public void findAssets(String assetId){

        List<IpData> ipDataList = ipDataRepository.findByAssetId(assetId);

        for(IpData obj: ipDataList){
            List<SubdomainPortData> subdomainPortDataList = obj.getPortScanData();
            String ipAddress = obj.getIpAddress();
            for(SubdomainPortData obj2 : subdomainPortDataList){
                String service = obj2.getPortService();
                switch (service) {
                    case "mysql" -> testMysql(ipAddress);
                    case "postgresql" -> testPostgreSQL(ipAddress);
                    case "mongodb" -> System.out.println("mongodb");
                    case "ftp" -> System.out.println("mongodb");
                    default -> System.out.println("there is no service for scanning");
                }



            }

            }
    }






    public boolean testConnection(String service,String ipAddress) {



    switch (service){
        case "mysql":
            testMysql(ipAddress);
            break;

        case "postgresql":
            testPostgreSQL(ipAddress);
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


    public boolean testMysql(String ipAddress){

        String url = "jdbc:mysql://" + ipAddress;
        String username = "root";
        String password = "root";

        try (Connection conn = DriverManager.getConnection(url,username,password);
             Statement stmt = conn.createStatement()) {

            String sql = "SHOW DATABASES;";
            ResultSet resultSet = stmt.executeQuery(sql);

            while (resultSet.next()) {
                String databaseName = resultSet.getString(1);
                System.out.println(databaseName);
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }


    public boolean testPostgreSQL(String ipAddress) {

        String url = "jdbc:postgresql://" + ipAddress+"/";
        String username = "postgres";
        String password = "root";

        Connection conn = null;

        try {

            Class.forName("org.postgresql.Driver");

            conn = DriverManager.getConnection(url, username, password);
            System.out.println("Connection to PostgreSQL database established successfully.");

            return true;

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } finally {

            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }


        return false;
    }






}
