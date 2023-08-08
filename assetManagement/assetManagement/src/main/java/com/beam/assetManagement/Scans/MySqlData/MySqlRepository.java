package com.beam.assetManagement.Scans.MySqlData;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MySqlRepository extends MongoRepository <MySqlReport,String> {

}
