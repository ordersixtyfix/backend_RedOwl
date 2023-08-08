package com.beam.assetManagement.firm;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FirmRepository extends MongoRepository<Firm,String> {


    public boolean existsByFirmName(String firmName);




}
