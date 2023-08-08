package com.beam.assetManagement.Scans.PostgreSqlData;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
@Document(collection = "PostGreSqlReport")
@Data
@SuperBuilder
@NoArgsConstructor
@TypeAlias("PostGreSQL")

public class PostGreSqlReport {
    @Id
    private String postGreSqlId;
    private List<String> databaseNames;
}
