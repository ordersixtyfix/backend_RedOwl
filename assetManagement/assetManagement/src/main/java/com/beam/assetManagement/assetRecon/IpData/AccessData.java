package com.beam.assetManagement.assetRecon.IpData;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data

public class AccessData {

    private boolean accessData;

    private String service;

    private String username;

    private String password;
}
