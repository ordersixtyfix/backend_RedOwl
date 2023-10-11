package com.beam.assetManagement.PayloadFile;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PayloadFileRepository extends MongoRepository<PayloadFile, String> {
    List<PayloadFile> findByUserId(String userId);

    Optional<PayloadFile> findTopByOrderByUploadDateDesc();

    Optional<PayloadFile> findFirstByListTypeOrderByUploadDateDesc(String listType);





}
