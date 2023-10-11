package com.beam.assetManagement.assetRecon.ServiceEnum.MySqlData;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MySqlRepository extends MongoRepository <MySqlReport,String> {
    Optional<MySqlReport> findByIpAddress(String ipAddress);
    List<MySqlReport> findByAssetIdAndFirmId(String assetId, String firmId);
}
