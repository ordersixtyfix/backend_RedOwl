package com.beam.assetManagement.Scans.FtpData;

import com.beam.assetManagement.assetRecon.IpData.IpData;
import com.beam.assetManagement.assetRecon.IpData.IpDataRepository;
import com.beam.assetManagement.common.GenericResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.UUID;
@Service
@RequiredArgsConstructor
public class FtpService {
    private final FtpReportRepository ftpReportRepository;
    private final IpDataRepository ipDataRepository;
    public GenericResponse<FtpReport> ftpResult(String assetId) {
            List<IpData> ipDataList = ipDataRepository.findByAssetId(assetId);
            FtpReport ftpReport = null;
            boolean successfulConnection = false;
            for (IpData ipData : ipDataList) {
                String ipAddress = ipData.getIpAddress();
                String[] files;
                String fileName = "Payloads/deneme.txt";
                Resource resource = new ClassPathResource(fileName);
                try (InputStream inputStream = resource.getInputStream();
                     BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
                    String password;
                    while ((password = br.readLine()) != null) {
                        String username = "ftp-user";
                        FTPClient ftpClient = new FTPClient();
                        ftpClient.connect(ipAddress);
                        if (ftpClient.login(username, password)) {
                            files = ftpClient.listNames();
                            String fileCount = String.valueOf(files != null ? files.length : 0);
                            String ftpId = UUID.randomUUID().toString();
                            boolean isConnected = ftpClient.isConnected();
                            boolean isFileExist = files != null && files.length > 0;
                            ftpReport = FtpReport.builder()
                                    .ftpId(ftpId)
                                    .ipAddress(ipAddress)
                                    .isConnected(isConnected)
                                    .isFilesExist(isFileExist)
                                    .userName(username)
                                    .password(password)
                                    .fileCount(fileCount)
                                    .build();
                            ftpReportRepository.save(ftpReport);
                        }
                    }
                } catch (IOException e) {
                    return new GenericResponse<>(522, null);
                }
            }
            return new GenericResponse<>(200, ftpReport); // Return the FtpReport with code 200 if at least one connection was successful
        }



    public FtpReport ftpScanTest(String ipAddress) throws IOException {
        String[] files;
        String fileName = "Payloads/deneme.txt";
        Resource resource = new ClassPathResource(fileName);
        InputStream inputStream = resource.getInputStream();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String password;

            while ((password = br.readLine()) != null) {
                String username = "ftp-user";
                FTPClient ftpClient = new FTPClient();
                ftpClient.connect(ipAddress);
                if (ftpClient.login(username, password)) {
                    files = ftpClient.listNames();
                    String fileCount = String.valueOf(files != null ? files.length : 0);
                    String ftpId = UUID.randomUUID().toString();
                    boolean isConnected = ftpClient.isConnected();
                    boolean isFileExist = files != null && files.length > 0;
                    FtpReport ftpReport = FtpReport.builder()
                            .ftpId(ftpId)
                            .ipAddress(ipAddress)
                            .isConnected(isConnected)
                            .isFilesExist(isFileExist)
                            .userName(username)
                            .password(password)
                            .fileCount(fileCount)
                            .build();
                    ftpReportRepository.save(ftpReport);
                    return ftpReport;
                }
            }
            return null;
        }
    }
    public List<FtpReport> getAllFtpReports() {
        return ftpReportRepository.findAll();
    }
}





