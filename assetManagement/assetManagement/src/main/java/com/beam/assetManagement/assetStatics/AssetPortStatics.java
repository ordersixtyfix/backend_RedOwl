package com.beam.assetManagement.assetStatics;

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