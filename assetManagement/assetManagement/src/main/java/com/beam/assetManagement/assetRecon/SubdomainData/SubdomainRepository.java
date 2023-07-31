package com.beam.assetManagement.assetRecon.SubdomainData;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository

public interface SubdomainRepository extends MongoRepository<SubdomainData,String> {

    Optional<SubdomainData> findByAssetName(String name);

    Optional<SubdomainData> findById(String id);

    boolean existsByAssetName(String name);
}
