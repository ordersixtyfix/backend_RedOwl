package com.beam.assetManagement.generateScanReport;

import com.beam.assetManagement.assetRecon.IpData.AccessData;
import com.beam.assetManagement.assetRecon.IpData.SubdomainPortData;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Set;
@Data
@AllArgsConstructor
public class scanResultRequest {

    private String assetId;
    private String ipAddress;
    private Set<String> subdomainShareIp;
    private List<SubdomainPortData> PortScanData;
    private List<AccessData> accessData;
    private String firmId;
    private Date scanDate;
    private Set<String> vulns;
    private String asn;
    private String os;




    private String city;
    private String country_code;
    private String region_code;
    private double longitude;
    private double latitude;
    private String isp;
}
