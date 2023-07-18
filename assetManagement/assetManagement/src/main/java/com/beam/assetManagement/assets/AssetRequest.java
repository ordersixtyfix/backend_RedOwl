package com.beam.assetManagement.assets;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class AssetRequest {



    private String assetName;

    private String assetLocation;

    private String assetIpAddress;

    private String assetDomain;


}
