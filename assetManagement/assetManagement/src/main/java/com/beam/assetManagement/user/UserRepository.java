package com.beam.assetManagement.user;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.Optional;

@Repository
//@Transactional(readOnly = true)

@CrossOrigin("localhost:4200")
public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByEmail(String email);

    Optional<User> findById(String userId);

    Optional<User> findFirstByFirmId(String firmId);






    Optional<User> findByPasswordAndEmail(String password,String email);

}
