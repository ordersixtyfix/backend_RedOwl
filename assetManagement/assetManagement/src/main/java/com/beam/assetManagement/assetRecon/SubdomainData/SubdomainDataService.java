package com.beam.assetManagement.assetRecon.SubdomainData;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

import static com.beam.assetManagement.assetRecon.AssetEnumerationService.assetDomainName;

@Service
@AllArgsConstructor
public class SubdomainDataService {
    private final SubdomainRepository subdomainRepository;

    public void SaveSubdomainData(Set<String> subdomainIds, String assetId) {

        Set<String> subdomainIdList = subdomainIds;
        SubdomainData subdomainData = new SubdomainData(subdomainIdList,assetDomainName,assetId);
        subdomainRepository.save(subdomainData);

    }
}
