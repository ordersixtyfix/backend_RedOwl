package com.beam.assetManagement.assets;


import com.beam.assetManagement.assetRecon.Base.Base;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "Assets")
@TypeAlias("Asset")
@SuperBuilder
public class Asset extends Base {


    private String assetName;

    private String assetIpAddress;


    private String assetLocation;

    private String assetDomain;

    private AssetData assetData;

    private String firmId;


}
