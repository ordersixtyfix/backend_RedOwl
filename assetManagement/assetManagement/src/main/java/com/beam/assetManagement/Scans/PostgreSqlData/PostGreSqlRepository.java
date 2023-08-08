package com.beam.assetManagement.Scans.PostgreSqlData;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostGreSqlRepository extends MongoRepository<PostGreSqlReport,String> {
}
