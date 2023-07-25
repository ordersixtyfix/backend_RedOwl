package com.beam.assetManagement.assetRecon.IpData;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor

public class SubdomainPortData {



    private String port;
    private String portState;
    private String portService;
    private boolean accessibleData;





}
