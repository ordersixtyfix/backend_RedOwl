package com.beam.assetManagement.assetRecon.ServiceEnum.FtpData;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface FtpReportRepository extends MongoRepository<FtpReport,String> {
    Optional<FtpReport> findByIpAddress(String ipAddress);
    List<FtpReport> findByAssetId(String assetId);;
}
