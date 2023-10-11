package com.beam.assetManagement.PayloadFile;

import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor

public class PayloadFileService {

    private final PayloadFileRepository payloadFileRepository;
    private final GridFsTemplate gridFsTemplate;
    private final MongoTemplate mongoTemplate;
    private final long MAX_FILE_SIZE = 5191680;

    public String uploadTextFile(MultipartFile file,String userId, String serviceType,String listType) throws IOException{


        if (!file.getOriginalFilename().endsWith(".txt")) {
            throw new IllegalArgumentException("Unsupported File Type");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new FileSizeLimitExceededException("File size exceed", file.getSize(), 5191681);
        }
        ObjectId fileId = gridFsTemplate.store(file.getInputStream(), file.getOriginalFilename());
        PayloadFile payloadFile = PayloadFile.builder()
                .serviceType(serviceType)
                .filename(file.getOriginalFilename())
                .userId(userId)
                .listType(listType)
                .content(new String(file.getBytes()))
                .id(UUID.randomUUID().toString())
                .uploadDate(new java.sql.Timestamp(new Date().getTime()))
                .build();

        mongoTemplate.save(payloadFile);
        return fileId.toString();
    }

    public List<PayloadFile> getFilesByUserId(String userId) {
        return payloadFileRepository.findByUserId(userId);
    }
}
