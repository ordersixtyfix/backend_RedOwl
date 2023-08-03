package com.beam.assetManagement.firm;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FirmRequest {
    private String firmName;
    private String firmLocation;
}
