package com.beam.assetManagement.assetRecon.ServiceEnum.ServiceData;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
public enum ServiceType {
    FTP(1),
    POSTGRESQL(2),
    MYSQL(3),
    ALL(4);

    private int value;
}
