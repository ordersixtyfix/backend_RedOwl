package com.beam.assetManagement.Scans.FtpData;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "FtpReports")
@SuperBuilder
@TypeAlias("ftpReport")
@Data
@NoArgsConstructor
public class FtpReport {
    @Id
    private String ftpId;
    private String ipAddress;
    private boolean isConnected;
    private boolean isFilesExist;
    private String userName;
    private String password;
    private String fileCount;
}
