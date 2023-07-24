package com.beam.assetManagement.assetRecon.ServiceEnum;

public class ServiceEnum {


    public boolean testConnection(String service) {

    switch (service){
        case "mysql":
            System.out.println("mysql");
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
