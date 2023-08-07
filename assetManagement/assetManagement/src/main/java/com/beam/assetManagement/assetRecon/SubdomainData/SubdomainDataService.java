package com.beam.assetManagement.assetRecon.SubdomainData;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

import static com.beam.assetManagement.assetRecon.AssetEnumerationService.assetDomainName;

@Service
@RequiredArgsConstructor
public class SubdomainDataService {

    private final SubdomainRepository subdomainRepository;


    public void SaveSubdomainData(Set<String> subdomainIds, String assetId, String firmId) {

        Set<String> subdomainIdList = subdomainIds;

        SubdomainData subdomainData = SubdomainData.builder()
                .subdomainIds(subdomainIdList)
                .assetName(assetDomainName)
                .firmId(firmId)
                .id(assetId).build();


        subdomainRepository.save(subdomainData);

    }
}
