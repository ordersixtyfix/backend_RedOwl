package com.beam.assetManagement.assetRecon.IpData;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Document(collection = "IpData")
@TypeAlias("IpData")
public class IpData {
    @Id
    private String id;

    private String assetId;

    private String ipAddress;

    private Set<String> subdomainShareIp;

    private List<SubdomainPortData> PortScanData;

    private List<AccessData> accessData;




    IpData(String IpAddress, String subdomainShareIp,String assetId){
        this.id=UUID.randomUUID().toString();

        this.ipAddress=IpAddress;
        this.subdomainShareIp = new HashSet<>();
        this.subdomainShareIp.add(subdomainShareIp);
        this.assetId = assetId;
        this.accessData = new ArrayList<>();
    }



    public void addShareSubdomains(String  subdomain) {
        subdomainShareIp.add(subdomain);
    }

    public void insertPortData(List<SubdomainPortData> subdomainPortDataList){
        this.PortScanData=subdomainPortDataList;
    }

    public Set<String> getSharedDomains(){
        return subdomainShareIp;
    }



}
