package com.beam.assetManagement.assetRecon.SubdomainData;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Data

@NoArgsConstructor
@Document(collection = "SubdomainData")
@TypeAlias("SubdomainData")
public class SubdomainData {

    @Id
    private String domainId;

    private String assetName;

    private Set<String> subdomainIds;




    SubdomainData(Set<String> subdomainIds, String assetName, String domainId){

        this.subdomainIds=subdomainIds;
        this.assetName=assetName;
        this.domainId=domainId;

    }











}
