package com.beam.assetManagement.assetRecon.SubdomainDataDetails;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubdomainDetailsRepository extends MongoRepository<SubdomainDataDetails,String> {



    boolean existsBySubdomain(String line);

    Optional<SubdomainDataDetails> findBySubdomain(String subdomain);

    Optional<SubdomainDataDetails> findBySubdomainId(String subdomainId);
}
