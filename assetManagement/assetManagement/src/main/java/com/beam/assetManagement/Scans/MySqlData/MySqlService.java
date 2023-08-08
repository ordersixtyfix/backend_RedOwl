package com.beam.assetManagement.Scans.MySqlData;

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
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class MySqlService {
    private final MySqlRepository mySqlRepository;

    public Object testMysql(String ipAddress) throws IOException {
        String url = "jdbc:mysql://" + ipAddress;
        String username = "superadmin";
        String password;
        List<String> databaseNames = new ArrayList<>();
        Resource resource = new ClassPathResource("Payloads/deneme.txt");
        BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));
        String line;
        boolean successfulConnection = false;
        while ((line = reader.readLine()) != null) {
            password = line.trim(); // Read the next line from deneme.txt
            try (Connection conn = DriverManager.getConnection(url, username, password);
                 Statement stmt = conn.createStatement()) {
                String sql = "SHOW DATABASES;";
                ResultSet resultSet = stmt.executeQuery(sql);
                while (resultSet.next()) {
                    String databaseName = resultSet.getString(1);
                    databaseNames.add(databaseName);
                }
                successfulConnection = true;
            } catch (SQLException e) {
                // Connection failed, continue with the next password
                System.err.println("Failed to connect with password: " + password);
            }
        }
        if (!successfulConnection) {
            return new GenericResponse<>(522, null); // Return null if no successful connections were made
        }

        String mySqlId = UUID.randomUUID().toString();
        MySqlReport mySqlReport = MySqlReport.builder()
                .mySqlId(mySqlId)
                .databaseNames(databaseNames)
                .build();
        mySqlRepository.save(mySqlReport);
        reader.close(); // Close the reader after use
        return new GenericResponse<>(200,mySqlReport);
    }
}
