package com.beam.assetManagement.assets;

import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
@Data
public class AssetData{



    private List<String> registrarData;

    private List<String> nameServersData;





    // Constructor with arguments
    public AssetData(List<String> registrarData, List<String> nameServersData) {
        this.registrarData = registrarData;
        this.nameServersData=nameServersData;




    }





}
