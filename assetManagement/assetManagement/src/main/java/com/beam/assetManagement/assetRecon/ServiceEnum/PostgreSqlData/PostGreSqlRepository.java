package com.beam.assetManagement.assetRecon.ServiceEnum.PostgreSqlData;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostGreSqlRepository extends MongoRepository<PostGreSqlReport,String> {
    Optional<PostGreSqlReport> findByIpAddress(String ipAddress);
    List<PostGreSqlReport> findByAssetId(String assetId);
}
