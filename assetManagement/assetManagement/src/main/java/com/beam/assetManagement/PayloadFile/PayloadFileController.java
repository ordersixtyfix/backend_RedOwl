package com.beam.assetManagement.PayloadFile;

import com.beam.assetManagement.common.GenericResponse;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class PayloadFileController {

    private final PayloadFileService payloadFileService;

    @PostMapping("/upload/{userId}/{serviceType}/{listType}")
    public GenericResponse<Object> uploadTextFile(@RequestParam("file") MultipartFile file, @PathVariable String userId, @PathVariable String serviceType, @PathVariable String listType) {
        try {
            payloadFileService.uploadTextFile(file, userId, serviceType,listType);
            return new GenericResponse<>().setCode(200).setData(listType);
        } catch (IllegalArgumentException e) {
            return new GenericResponse<>().setCode(415).setData(listType);
        }catch (FileSizeLimitExceededException e){
            return new GenericResponse<>().setCode(413).setData(listType);
        } catch (IOException e) {
            return new GenericResponse<>().setCode(400).setData(listType);
        }
    }

    @GetMapping("/{userId}")
    public List<PayloadFile> getFilesByUserId(@PathVariable String userId) {
        return payloadFileService.getFilesByUserId(userId);
    }

}
