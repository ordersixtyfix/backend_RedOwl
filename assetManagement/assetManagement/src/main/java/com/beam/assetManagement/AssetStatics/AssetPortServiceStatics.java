package com.beam.assetManagement.AssetStatics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AssetPortServiceStatics {

    private int openHttp=0;

    private int openSmtp=0;

    private int openFtp=0;

    private int openSsh=0;

    private int openSmb=0;

    private int openDns=0;

    private int openTelnet=0;

    private int openTftp=0;

    public void incrementOpenHttp() {
        openHttp++;
    }

    public void incrementOpenSmtp() {
        openSmtp++;
    }

    public void incrementOpenFtp() {
        openFtp++;
    }

    public void incrementOpenSsh() {
        openSsh++;
    }

    public void incrementOpenSmb() {
        openSmb++;
    }

    public void incrementOpenDns() {
        openDns++;
    }

    public void incrementOpenTelnet() {
        openTelnet++;
    }

    public void incrementOpenTftp() {
        openTftp++;
    }





}
