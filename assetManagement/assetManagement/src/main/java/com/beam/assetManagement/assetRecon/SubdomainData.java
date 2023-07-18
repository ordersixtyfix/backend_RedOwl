package com.beam.assetManagement.assetRecon;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data

@NoArgsConstructor
@EqualsAndHashCode
@Document(collection = "SubdomainData")
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
