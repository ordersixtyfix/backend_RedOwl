package com.beam.assetManagement.assetRecon;

import com.beam.assetManagement.assetRecon.IpData.IpData;
import com.beam.assetManagement.assetRecon.IpData.IpDataRepository;
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

        private final IpDataRepository ipDataRepository;


    @Scheduled(fixedRate =  12000000)
    public void sendScanReport(){
        try {
            List<IpData> assetList = ipDataRepository.findAll();




        } catch (Exception e) {
            e.printStackTrace();

        }
    }




    }



