package com.beam.assetManagement.assetRecon;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.awt.desktop.OpenFilesEvent;
import java.util.List;
import java.util.Optional;

public interface IpDataRepository extends MongoRepository<IpData,String> {



    List<IpData> findByAssetId(String assetId);

    IpData findByIpAddress(String ip);

    void deleteAllByAssetId(String assetId);

    Optional<IpData> findByAssetIdAndIpAddress(String assetId,String ipAddress);



    boolean existsByAssetId(String assetId);
}
