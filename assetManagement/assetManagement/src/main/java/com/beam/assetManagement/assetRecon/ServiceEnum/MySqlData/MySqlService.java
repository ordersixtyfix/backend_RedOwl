package com.beam.assetManagement.assetRecon.ServiceEnum.MySqlData;

import com.beam.assetManagement.PayloadFile.PayloadFile;
import com.beam.assetManagement.PayloadFile.PayloadFileRepository;
import com.beam.assetManagement.common.GenericResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class MySqlService {
    private final MySqlRepository mySqlRepository;

    private final PayloadFileRepository payloadFileRepository;

    public GenericResponse<MySqlReport> testMysql(String assetId, String firmId, String ipAddress) throws IOException {
        MySqlReport mySqlReport = null;
        String url = "jdbc:mysql://" + ipAddress;
        String username;
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


        boolean isConnected = false;
        String mySqlId = UUID.randomUUID().toString();
        List<String> databaseNames = new ArrayList<>();


        while ((username = readerPassword.readLine()) != null) {
            while((password = readerUsername.readLine()) != null) {
                try (Connection conn = DriverManager.getConnection(url, username, password); Statement stmt = conn.createStatement()) {
                    isConnected = true;
                    String sql = "SHOW DATABASES;";
                    ResultSet resultSet = stmt.executeQuery(sql);
                    while (resultSet.next()) {
                        String databaseName = resultSet.getString(1);
                        databaseNames.add(databaseName);
                    }
                    break;
                } catch (SQLException e) {

                    System.err.println("Failed to connect with password: " + password);
                }
            }
        }
        readerUsername.close();
        readerPassword.close();
        if (isConnected) {
            Optional<MySqlReport> existingReport = mySqlRepository.findByIpAddress(ipAddress);
            if (existingReport.isPresent()) {
                mySqlReport = existingReport.get();
                mySqlReport.setAssetId(assetId);
                mySqlReport.setFirmId(firmId);
                mySqlReport.setUsername(username);
                mySqlReport.setPassword(password);
                mySqlReport.setIpAddress(ipAddress);
                mySqlReport.setDatabaseNames(databaseNames);
                mySqlRepository.save(mySqlReport);
            } else {
                mySqlReport = MySqlReport.builder()
                        .id(mySqlId).assetId(assetId)
                        .firmId(firmId).ipAddress(ipAddress)
                        .databaseNames(databaseNames)
                        .build();
                mySqlRepository.save(mySqlReport);
            }
        }

        return isConnected ? new GenericResponse<>(200, mySqlReport) : new GenericResponse<>(400, mySqlReport);
    }

    public List<MySqlReport> findByAssetIdAndFirmId(String assetId,String firmId) {


        List<MySqlReport> reports = mySqlRepository.findByAssetIdAndFirmId(assetId, firmId);
        return reports;
    }


}
