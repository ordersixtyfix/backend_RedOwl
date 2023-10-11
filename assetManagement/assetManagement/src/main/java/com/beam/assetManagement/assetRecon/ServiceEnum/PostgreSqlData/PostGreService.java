package com.beam.assetManagement.assetRecon.ServiceEnum.PostgreSqlData;

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

@Service
@RequiredArgsConstructor
public class PostGreService {
    private final PostGreSqlRepository postGreSqlRepository;

    private final PayloadFileRepository payloadFileRepository;

    public GenericResponse<PostGreSqlReport> testPostgreSQL(String assetId, String firmId, String ipAddress)
            throws IOException, ClassNotFoundException {
        PostGreSqlReport postGreSqlReport = null;
        List<String> databaseNames;
        databaseNames = new ArrayList<>();
        String url = "jdbc:postgresql://"+ipAddress+"/postgres";
        String username;
        String password;


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
        String postGreSqlId = UUID.randomUUID().toString();

        while ((username = readerUsername.readLine()) != null) {
            while((password = readerPassword.readLine()) != null){
                Class.forName("org.postgresql.Driver");
                try (Connection conn = DriverManager.getConnection(url, username, password)) {
                    isConnected = true;
                    DatabaseMetaData metaData = conn.getMetaData();
                    ResultSet resultSet = metaData.getCatalogs();
                    while (resultSet.next()) {
                        String databaseName = resultSet.getString("TABLE_CAT");
                        databaseNames.add(databaseName);
                    }
                } catch (SQLException e) {
                    System.err.println("Failed to connect with password: " + password);
                }
            }
        }



        readerUsername.close();
        readerPassword.close();

        if (isConnected) {
            Optional<PostGreSqlReport> existingReport = postGreSqlRepository.findByIpAddress(ipAddress);
            if (existingReport.isPresent()) {
                postGreSqlReport = existingReport.get();
                postGreSqlReport.setAssetId(assetId);
                postGreSqlReport.setFirmId(firmId);
                postGreSqlReport.setIpAddress(ipAddress);
                postGreSqlReport.setDatabaseNames(databaseNames);
                postGreSqlRepository.save(postGreSqlReport);
            } else {
                postGreSqlReport = PostGreSqlReport.builder()
                        .id(postGreSqlId)
                        .assetId(assetId)
                        .firmId(firmId)
                        .ipAddress(ipAddress)
                        .databaseNames(databaseNames)
                        .build();
                postGreSqlRepository.save(postGreSqlReport);
            }
        }
        return isConnected
                ? new GenericResponse<>(200, postGreSqlReport)
                : new GenericResponse<>(400, postGreSqlReport);
    }

    public List<PostGreSqlReport> findByAssetId(String assetId) {
        List<PostGreSqlReport> reports = postGreSqlRepository.findByAssetId(assetId);
        return reports;
    }
}