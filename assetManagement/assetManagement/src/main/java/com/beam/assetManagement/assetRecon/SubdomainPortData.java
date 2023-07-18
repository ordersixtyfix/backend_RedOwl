package com.beam.assetManagement.assetRecon;

import lombok.Data;

@Data
public class SubdomainPortData {



    private String port;
    private String portState;
    private String portService;

    public SubdomainPortData(String port, String portState, String portService){
        this.port = port;
        this.portState=portState;
        this.portService=portService;
    }



}
