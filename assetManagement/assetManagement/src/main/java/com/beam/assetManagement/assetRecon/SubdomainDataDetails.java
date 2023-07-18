package com.beam.assetManagement.assetRecon;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Document(collection = "SubdomainDataDetails")
public class SubdomainDataDetails {

    @Id
    private String subdomainId;
    private String subdomain;

    private boolean isRedirected = false;

    private String redirectDomain;

    private boolean isHostDown = false;





    SubdomainDataDetails(String subdomain, List<SubdomainPortData> subdomainPortData){
        this.subdomainId = UUID.randomUUID().toString();
        this.subdomain= subdomain;

    }

    SubdomainDataDetails(String subdomain,boolean isHostDown){
        this.subdomainId = UUID.randomUUID().toString();
        this.subdomain = subdomain;
        this.isHostDown = isHostDown;
    }

    SubdomainDataDetails(String subdomain,List<SubdomainPortData> subdomainPortData,boolean isRedirected, String redirectDomain) {
        this.subdomainId = UUID.randomUUID().toString();
        this.subdomain= subdomain;

        this.isRedirected = isRedirected;
        this.redirectDomain = redirectDomain;
    }
    public boolean getHostDown(){
        return isHostDown;
    }




}
