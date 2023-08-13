package com.beam.assetManagement.assetStatics;

import com.beam.assetManagement.assetRecon.IpData.IpData;
import com.beam.assetManagement.assetRecon.IpData.IpDataRepository;
import com.beam.assetManagement.assetRecon.IpData.SubdomainPortData;
import com.beam.assetManagement.assetRecon.SubdomainData.SubdomainData;
import com.beam.assetManagement.assetRecon.SubdomainData.SubdomainRepository;
import com.beam.assetManagement.assets.Asset;
import com.beam.assetManagement.assets.AssetRepository;
import com.beam.assetManagement.user.User;
import com.beam.assetManagement.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AssetStaticsService {


    private final IpDataRepository ipDataRepository;

    private final UserRepository userRepository;

    private final AssetRepository assetRepository;

    private final SubdomainRepository subdomainRepository;

    public AssetPortStatics getPortStatics(String firmId, String userId) {
        int openPortCount = 0;
        int filteredPortCount = 0;
        int closedPortCount = 0;

        String role = getUserRole(userId);
        List<IpData> ipDataList;
        if (role == "SUPER_USER") {
            ipDataList = ipDataRepository.findAll();
        } else {
            ipDataList = ipDataRepository.findByFirmId(firmId);
        }
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


    public AssetPortServiceStatics getAssetPortServiceStatics(String userId, String firmId) {

        AssetPortServiceStatics assetPortServiceStatics = new AssetPortServiceStatics();
        String role = getUserRole(userId);
        List<IpData> ipDataList;
        if (role == "SUPER_USER") {
            ipDataList = ipDataRepository.findAll();
        } else {
            ipDataList = ipDataRepository.findByFirmId(firmId);
        }
        for (IpData ipData : ipDataList) {
            List<SubdomainPortData> portScanDataList = ipData.getPortScanData();
            for (SubdomainPortData subdomainPortData : portScanDataList) {
                String state = subdomainPortData.getPortState();
                String service = subdomainPortData.getPortService();
                if (state.equals("open")) {
                    switch (service) {
                        case "http":
                            assetPortServiceStatics.incrementOpenHttp();
                            break;
                        case "https":
                            assetPortServiceStatics.incrementOpenHttp();
                            break;
                        case "smtp":
                            assetPortServiceStatics.incrementOpenSmtp();
                            break;
                        case "ftp":
                            assetPortServiceStatics.incrementOpenFtp();
                            break;
                        case "ssh":
                            assetPortServiceStatics.incrementOpenSsh();
                            break;
                        case "smb":
                            assetPortServiceStatics.incrementOpenSmb();
                            break;
                        case "dns":
                            assetPortServiceStatics.incrementOpenDns();
                            break;
                        case "telnet":
                            assetPortServiceStatics.incrementOpenTelnet();
                            break;
                        case "tftp":
                            assetPortServiceStatics.incrementOpenTftp();
                            break;
                        default:
                            break;
                    }
                }
            }

        }
        return assetPortServiceStatics;
    }


    public long getDnsServerCount(String userId, String firmId) {
        long dnsCount = 0;
        String role = getUserRole(userId);
        List<Asset> assetList;
        if (role == "SUPER_USER") {
            assetList = assetRepository.findAll();
        } else {
            assetList = assetRepository.findByFirmId(firmId);
        }
        for (Asset asset : assetList) {
            dnsCount = asset.getAssetData().getNameServersData().stream().count();
        }
        return dnsCount;


    }


    public long getSubdomainCount(String userId, String firmId) {
        long subdomainCount = 0;
        String role = getUserRole(userId);
        List<SubdomainData> subdomainDataList;
        if (role == "SUPER_USER") {
            subdomainDataList = subdomainRepository.findAll();
            for (SubdomainData subdomainData : subdomainDataList) {
                subdomainCount += subdomainData.getSubdomainIds().size();
            }

        } else {
            subdomainDataList = subdomainRepository.findByFirmId(firmId);
            for (SubdomainData subdomainData : subdomainDataList) {
                subdomainCount = subdomainData.getSubdomainIds().size();
            }
        }

        return subdomainCount;


    }


    public Map<String, Long> getAssetLocationCount(String firmId,String userId) {
        try {
            List<Asset> assets;
            String role = getUserRole(userId);
            List<IpData> ipDataList;
            if (role == "SUPER_USER") {
                assets = assetRepository.findAll();
            } else {
                assets = assetRepository.findByFirmId(firmId);
            }

            Map<String, Long> locationCountMap = new HashMap<>();


            for (Asset asset : assets) {
                String location = asset.getAssetLocation();
                locationCountMap.put(location, locationCountMap.getOrDefault(location, 0L) + 1);
            }

            log.info(locationCountMap.toString());

            return locationCountMap;
        } catch (Exception e) {
            throw e;
        }
    }










    public String getUserRole(String userId) {

        Optional<User> user = userRepository.findById(userId);

        String role = user.get().getAppUserRole().toString();

        return role;
    }
}
