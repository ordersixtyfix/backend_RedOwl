package com.beam.assetManagement.AssetStatics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssetPortStatics {

    private int openPortCount;

    private int filteredPortCount;

    private int closedPortCount;

}