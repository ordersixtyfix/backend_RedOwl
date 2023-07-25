package com.beam.assetManagement.assetRecon.ServiceEnum;


import org.springframework.stereotype.Service;

import java.sql.*;


@Service
public class ServiceEnum {


    public boolean testConnection(String service,String ipAddress) {

        System.out.println("testConnection-BREAKPOINT");

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

        String url = "jdbc:postgresql://" + ipAddress;
        String username = "postgres";

        Connection c = null;
        Statement stmt = null;


        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager
                    .getConnection("jdbc:postgresql://localhost:5432/testdb",
                            "newtest", "555");
            System.out.println("Opened database successfully");

            stmt = c.createStatement();
            String sql = "CREATE TABLE BUSINESS " +
                    "(ID INT PRIMARY KEY     NOT NULL," +
                    " NAME           TEXT    NOT NULL, " +
                    " AGE            INT     NOT NULL, " +
                    " ADDRESS        CHAR(50), " +
                    " SALARY         REAL)";
            stmt.executeUpdate(sql);
            stmt.close();
            c.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            System.exit(0);
        }

        return false;
    }





}
