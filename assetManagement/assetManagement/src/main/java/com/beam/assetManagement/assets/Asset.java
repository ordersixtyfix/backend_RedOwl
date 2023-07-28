package com.beam.assetManagement.assets;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "Assets")
@TypeAlias("Asset")

public class Asset {


    @Id
    private String assetId;

    private String assetName;

    private String assetIpAddress;



    private String assetLocation;

    private String assetDomain;

    private AssetData assetData;


    public Asset(String assetName,String assetIpAddress, String assetLocation,String assetDomain){



        this.assetId = UUID.randomUUID().toString();
        this.assetName=assetName;
        this.assetIpAddress=assetIpAddress;
        this.assetLocation=assetLocation;
        this.assetDomain=assetDomain;
        this.assetData = new AssetData(new ArrayList<>(),new ArrayList<>());



    }















}
