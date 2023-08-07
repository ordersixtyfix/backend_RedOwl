package com.beam.assetManagement.assetRecon;

import com.beam.assetManagement.assets.Asset;
import com.beam.assetManagement.assets.AssetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class ScheduledScanService {
        private final AssetEnumerationService assetEnumerationService;

        private final AssetRepository assetRepository;



        @Scheduled(fixedRate =  1200000)
        public void executeAssetEnumeration() {
            try {
                List<Asset> assetList = assetRepository.findAll();

                for (Asset asset : assetList) {
                    String assetId = asset.getId();
                    String firmId = asset.getFirmId();
                    assetEnumerationService.setAsset(assetId, firmId);
                }


            } catch (Exception e) {
                e.printStackTrace();

            }
        }
    }



