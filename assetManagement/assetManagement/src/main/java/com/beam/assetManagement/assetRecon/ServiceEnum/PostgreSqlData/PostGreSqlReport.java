package com.beam.assetManagement.assetRecon.ServiceEnum.PostgreSqlData;

import com.beam.assetManagement.assetRecon.Base.Base;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
@Document(collection = "PostGreSqlReport")
@Data
@SuperBuilder
@NoArgsConstructor
@TypeAlias("PostGreSQL")

public class PostGreSqlReport extends Base {
    private String assetId;
    private String firmId;
    private List<String> databaseNames;
    private String ipAddress;

}
