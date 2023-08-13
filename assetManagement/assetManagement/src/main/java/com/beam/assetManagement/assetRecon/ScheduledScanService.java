package com.beam.assetManagement.assetRecon;

import com.beam.assetManagement.assets.AssetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
@Service
@RequiredArgsConstructor
public class ScheduledScanService {
        private final AssetEnumerationService assetEnumerationService;

        private final AssetRepository assetRepository;




    }



