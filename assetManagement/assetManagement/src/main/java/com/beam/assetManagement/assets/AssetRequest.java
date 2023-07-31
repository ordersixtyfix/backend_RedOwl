package com.beam.assetManagement.assets;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AssetRequest {


    private String assetName;

    private String assetLocation;

    private String assetIpAddress;

    private String assetDomain;

    private String userId;


}
