package com.beam.assetManagement.Scans.MySqlData;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
@Document(collection = "MySqlReports")
@Data
@NoArgsConstructor
@TypeAlias("MySqlReport")
@SuperBuilder
public class MySqlReport {
    @Id
    private String mySqlId;
    private List<String> databaseNames;

}
