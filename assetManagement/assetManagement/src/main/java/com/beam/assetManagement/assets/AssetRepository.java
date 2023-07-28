package com.beam.assetManagement.assets;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.Optional;

@Repository
@CrossOrigin("localhost:4200")
public interface AssetRepository extends MongoRepository<Asset,Integer> {

    Optional<Asset> findByAssetName(String assetName);
    Optional<Asset> findByAssetId(String assetId);



    Optional<Asset> deleteAssetByAssetId(String assetId);


}
