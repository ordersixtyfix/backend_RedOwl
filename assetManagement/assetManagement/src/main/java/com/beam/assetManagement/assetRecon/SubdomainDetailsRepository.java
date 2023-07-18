package com.beam.assetManagement.assetRecon;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.awt.desktop.OpenFilesEvent;
import java.util.Optional;
import java.util.Set;

@Repository
public interface SubdomainDetailsRepository extends MongoRepository<SubdomainDataDetails,String> {



    boolean existsBySubdomain(String line);

    Optional<SubdomainDataDetails> findBySubdomain(String subdomain);

    Optional<SubdomainDataDetails> findBySubdomainId(String subdomainId);
}
