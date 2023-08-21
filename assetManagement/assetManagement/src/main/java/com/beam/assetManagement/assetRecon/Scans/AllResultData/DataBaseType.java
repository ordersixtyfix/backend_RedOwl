package com.beam.assetManagement.assetRecon.Scans.AllResultData;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
public enum DataBaseType {
    FTP(1),
    POSTGRESQL(2),
    MYSQL(3),
    ALL(4);

    private int value;
}
