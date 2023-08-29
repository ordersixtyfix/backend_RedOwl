package com.beam.assetManagement.assets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Collation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AssetRepositoryImpl {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public AssetRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public List<Asset> findByAssetNameIgnoreCase(String assetName) {
        Collation collation = Collation.of("tr").caseLevel(false);
        Query query = Query.query(Criteria.where("assetName").is(assetName)).collation(collation);
        return mongoTemplate.find(query, Asset.class);
    }
}