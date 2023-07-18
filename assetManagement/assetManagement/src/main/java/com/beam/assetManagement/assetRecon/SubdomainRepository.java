package com.beam.assetManagement.assetRecon;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository

public interface SubdomainRepository extends MongoRepository<SubdomainData,String> {

    Optional<SubdomainData> findByAssetName(String name);

    Optional<SubdomainData> findByDomainId(String id);

    boolean existsByAssetName(String name);
}
