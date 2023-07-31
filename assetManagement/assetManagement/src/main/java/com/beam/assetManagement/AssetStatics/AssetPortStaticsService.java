package com.beam.assetManagement.AssetStatics;

import com.beam.assetManagement.assetRecon.IpData.IpData;
import com.beam.assetManagement.assetRecon.IpData.IpDataRepository;
import com.beam.assetManagement.assetRecon.IpData.SubdomainPortData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class AssetPortStaticsService {


    private final IpDataRepository ipDataRepository;

    public AssetPortStatics getAllPortStatics() {
        int openPortCount = 0;
        int filteredPortCount = 0;
        int closedPortCount = 0;


        List<IpData> ipDataList = ipDataRepository.findAll();

        for (IpData ipData : ipDataList) {
            List<SubdomainPortData> portScanDataList = ipData.getPortScanData();
            for (SubdomainPortData subdomainPortData : portScanDataList) {
                String state = subdomainPortData.getPortState();
                switch (state) {
                    case "open":
                        openPortCount++;
                        break;
                    case "filtered":
                        filteredPortCount++;
                        break;
                    case "closed":
                        closedPortCount++;
                        break;
                    default:
                        break;

                }
            }

        }

        return new AssetPortStatics(openPortCount, filteredPortCount, closedPortCount);


    }
}
