package com.beam.assetManagement.assets;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;
import java.util.Optional;

@Repository
@CrossOrigin("localhost:4200")
public interface AssetRepository extends MongoRepository<Asset, Integer> {

    Optional<Asset> findByAssetNameOrAssetDomainOrAssetIpAddress(String assetName, String assetDomain,
                                                                 String assetIpAddress);

    Optional<Asset> findById(String assetId);

    Page<Asset> findByFirmId(String userId, PageRequest pageRequest);

    List<Asset> findByFirmId(String userId);




    Optional<Asset> deleteAssetById(String assetId);


}
