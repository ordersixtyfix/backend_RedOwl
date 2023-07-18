package com.beam.assetManagement.assets;


import jdk.dynalink.linker.LinkerServices;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.el.parser.AstSetData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Document(collection = "Assets")
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
