package com.beam.assetManagement.assetRecon.Scans.MySqlData;

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

@RequiredArgsConstructor
@Service
public class MySqlService {
    private final MySqlRepository mySqlRepository;

    public GenericResponse<MySqlReport> testMysql(String assetId, String firmId, String ipAddress) throws IOException {
        MySqlReport mySqlReport = null;
        String url = "jdbc:mysql://192.168.43.48";
        String username = "superadmin";
        String password;
        String fileName = "Payloads/deneme.txt";
        boolean isConnected = false;
        String mySqlId = UUID.randomUUID().toString();
        List<String> databaseNames = new ArrayList<>();

        Resource resource = new ClassPathResource(fileName);
        BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));

        while ((password = reader.readLine()) != null) {
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
                // Connection failed, continue with the next password
                System.err.println("Failed to connect with password: " + password);
            }
        }
        reader.close();
        if (isConnected) {
            Optional<MySqlReport> existingReport = mySqlRepository.findByIpAddress(ipAddress);
            if (existingReport.isPresent()) {
                mySqlReport = existingReport.get();
                mySqlReport.setAssetId(assetId);
                mySqlReport.setFirmId(firmId);
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

    public List<MySqlReport> findByAssetId(String assetId) {
        List<MySqlReport> reports = mySqlRepository.findByAssetId(assetId);
        return reports;
    }

}
