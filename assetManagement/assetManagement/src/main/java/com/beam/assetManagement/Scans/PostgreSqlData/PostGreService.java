package com.beam.assetManagement.Scans.PostgreSqlData;

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

@Service
@RequiredArgsConstructor
public class PostGreService {
    private final PostGreSqlRepository postGreSqlRepository;

    public GenericResponse<PostGreSqlReport> testPostgreSQL(String ipAddress) throws IOException, ClassNotFoundException {
        String url = "jdbc:postgresql://" + ipAddress + "/";
        String username = "postgres";
        List<String> databaseNames = new ArrayList<>();
        Resource resource = new ClassPathResource("Payloads/deneme.txt");
        BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));
        String line;
        PostGreSqlReport postGreSqlReport = null;
        while ((line = reader.readLine()) != null) {
            String password = line.trim();
            Class.forName("org.postgresql.Driver");
            try (Connection conn = DriverManager.getConnection(url, username, password)) {
                DatabaseMetaData metaData = conn.getMetaData();
                ResultSet resultSet = metaData.getCatalogs();
                while (resultSet.next()) {
                    String databaseName = resultSet.getString("TABLE_CAT");
                    databaseNames.add(databaseName);
                }
                String postGreSqlId = UUID.randomUUID().toString();
                postGreSqlReport = PostGreSqlReport.builder()
                        .postGreSqlId(postGreSqlId)
                        .databaseNames(databaseNames)
                        .build();
                postGreSqlRepository.save(postGreSqlReport);
                // Return the PostGreSqlReport with code 200 if connection was successful
            } catch (SQLException e) {
                return new GenericResponse<>(522, null);
            }
        }
        reader.close(); // Close the reader after use
        return new GenericResponse<>(200, postGreSqlReport);
    }
}

