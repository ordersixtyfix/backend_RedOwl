package com.beam.assetManagement.assetRecon.IpData;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IpDataRepository extends MongoRepository<IpData,String> {



    List<IpData> findByAssetId(String assetId);

    IpData findByIpAddress(String ip);

    void deleteAllByAssetId(String assetId);

    Optional<IpData> findByAssetIdAndIpAddress(String assetId,String ipAddress);

    List<IpData> findByFirmId(String firmId);


    boolean existsByAssetId(String assetId);
}
