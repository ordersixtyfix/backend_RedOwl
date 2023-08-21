package com.beam.assetManagement.assetRecon.Scans.FtpData;

import com.beam.assetManagement.assetRecon.Base.Base;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "FtpReports")
@SuperBuilder
@TypeAlias("ftpReport")
@Data
@NoArgsConstructor
public class FtpReport extends Base {
    private String assetId;
    private String firmId;
    private String ipAddress;
    private boolean isConnected;
    private String userName;
    private String password;
    private String fileCount;

}
