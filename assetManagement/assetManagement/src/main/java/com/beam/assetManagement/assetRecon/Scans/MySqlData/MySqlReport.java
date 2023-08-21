package com.beam.assetManagement.assetRecon.Scans.MySqlData;

import com.beam.assetManagement.assetRecon.Base.Base;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
@Document(collection = "MySqlReports")
@Data
@NoArgsConstructor
@TypeAlias("MySqlReport")
@SuperBuilder
public class MySqlReport extends Base {
    private String assetId;
    private String firmId;
    private String ipAddress;
    private List<String> databaseNames;
}
