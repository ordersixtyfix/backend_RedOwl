package com.beam.assetManagement.assetRecon.Scans.PostgreSqlData;

import com.beam.assetManagement.common.GenericResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostGreService {
    private final PostGreSqlRepository postGreSqlRepository;

    public GenericResponse<PostGreSqlReport> testPostgreSQL(String assetId, String firmId, String ipAddress)
            throws IOException, ClassNotFoundException {
        PostGreSqlReport postGreSqlReport = null;
        List<String> databaseNames;
        databaseNames = new ArrayList<>();
        String password;
        String username = "postgres";
        boolean isConnected = false;
        String postGreSqlId = UUID.randomUUID().toString();
        String url = "jdbc:postgresql://127.0.0.1/postgres";
        Resource resource = new ClassPathResource("Payloads/deneme.txt");
        BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));

        while ((password = reader.readLine()) != null) {
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
        reader.close();
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