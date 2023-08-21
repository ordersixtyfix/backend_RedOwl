package com.beam.assetManagement.assetRecon.Scans.FtpData;

import com.beam.assetManagement.common.GenericResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FtpService {
    private final FtpReportRepository ftpReportRepository;

    public GenericResponse<FtpReport> ftpResult(String assetId, String firmId, String ipAddress) throws IOException {
        boolean isLogin = false;
        FtpReport ftpReport = null;
        String[] files;
        String username = "ftp-user";
        String password;
        String fileName = "Payloads/deneme.txt";
        String ftpId = UUID.randomUUID().toString();
        Resource resource = new ClassPathResource(fileName);
        BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));
        FTPClient ftpClient = null;
        while ((password = reader.readLine()) != null) {
            ftpClient = new FTPClient();
            ftpClient.connect("192.168.43.48");
            isLogin = ftpClient.login(username, password);
            if (isLogin) {
                break;
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
                        .id(ftpId)
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





