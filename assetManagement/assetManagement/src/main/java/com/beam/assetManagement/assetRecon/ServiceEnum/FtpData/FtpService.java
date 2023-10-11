package com.beam.assetManagement.assetRecon.ServiceEnum.FtpData;

import com.beam.assetManagement.PayloadFile.PayloadFile;
import com.beam.assetManagement.PayloadFile.PayloadFileRepository;
import com.beam.assetManagement.common.GenericResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FtpService {
    private final FtpReportRepository ftpReportRepository;

    private final PayloadFileRepository payloadFileRepository;

    public GenericResponse<FtpReport> ftpResult(String assetId, String firmId, String ipAddress,String userId) throws IOException {
        boolean isLogin = false;
        FtpReport ftpReport = null;
        String[] files;
        String username = null;
        String password = null;

        Optional<PayloadFile> latestFileUsernameList = payloadFileRepository.findFirstByListTypeOrderByUploadDateDesc("username");
        Optional<PayloadFile> latestFilePasswordList = payloadFileRepository.findFirstByListTypeOrderByUploadDateDesc("password");

        BufferedReader readerUsername = null;
        BufferedReader readerPassword = null;
        if (latestFileUsernameList.isPresent()) {
            PayloadFile latestUsernameFile = latestFileUsernameList.get();
            String fileContent = latestUsernameFile.getContent();
            StringReader stringReader = new StringReader(fileContent);
            readerUsername = new BufferedReader(stringReader);
        }
        if (latestFilePasswordList.isPresent()) {
            PayloadFile latestPasswordFile = latestFilePasswordList.get();
            String fileContent = latestPasswordFile.getContent();
            StringReader stringReader = new StringReader(fileContent);
            readerPassword = new BufferedReader(stringReader);
        }

        FTPClient ftpClient = null;
        while ((username = readerPassword.readLine()) != null) {

            while((password = readerUsername.readLine()) != null) {


                ftpClient = new FTPClient();
                ftpClient.connect(ipAddress);
                isLogin = ftpClient.login(username, password);
                if (isLogin) {
                    break;
                }
            }
        }
        files = ftpClient.listNames();
        String fileCount = String.valueOf(files != null ? files.length : 0);

        if (isLogin) {
            Optional<FtpReport> existingReport = ftpReportRepository.findByIpAddress(ipAddress);
            if (existingReport.isPresent()) {
                ftpReport = existingReport.get();
                ftpReport.setAssetId(assetId);
                ftpReport.setFirmId(firmId);
                ftpReport.setIpAddress(ipAddress);
                ftpReport.setConnected(isLogin);
                ftpReport.setUserName(username);
                ftpReport.setPassword(password);
                ftpReport.setFileCount(fileCount);
                ftpReportRepository.save(ftpReport);
            } else {
                ftpReport = FtpReport.builder()
                        .id(UUID.randomUUID().toString())
                        .assetId(assetId)
                        .firmId(firmId)
                        .ipAddress(ipAddress)
                        .isConnected(isLogin)
                        .userName(username)
                        .password(password)
                        .fileCount(fileCount)
                        .build();
                ftpReportRepository.save(ftpReport);
            }
        }
        return isLogin
                ? new GenericResponse<>(200, ftpReport)
                : new GenericResponse<>(400, ftpReport);
    }

    public List<FtpReport> findByAssetId(String assetId) {
        List<FtpReport> reports = ftpReportRepository.findByAssetId(assetId);
        return reports;
    }

}





