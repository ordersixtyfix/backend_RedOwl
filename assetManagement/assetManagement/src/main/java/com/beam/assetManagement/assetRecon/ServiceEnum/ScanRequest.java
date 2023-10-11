package com.beam.assetManagement.assetRecon.ServiceEnum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScanRequest {
    private List<String> scanType;
    private  String scanSpeed;
}