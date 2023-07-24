package com.beam.assetManagement.assetRecon.IpData;

import com.beam.assetManagement.assetRecon.AssetEnumeration;
import com.beam.assetManagement.assetRecon.SubdomainDataDetails.SubdomainDataDetails;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
@Service
@AllArgsConstructor
public class IpDataService {

    @Autowired
    private  IpDataRepository ipDataRepository;

    private AssetEnumeration assetEnumeration;

    @Autowired
    public IpDataService(@Lazy AssetEnumeration assetEnumeration){
        this.assetEnumeration = assetEnumeration;
    }



    public void insertPortScanToObject(String assetId) throws IOException {

        List<IpData> ipDataList = ipDataRepository.findByAssetId(assetId);

        for(IpData obj : ipDataList){
            String ipAddress = obj.getIpAddress();
            List<SubdomainPortData> portData=assetEnumeration.getPortData(ipAddress);
            obj.insertPortData(portData);

            ipDataRepository.save(obj);

        }

    }

    public void getIpFromDataDetailsObject(Set<SubdomainDataDetails> subdomainDataDetailsList, String assetId)
            throws IOException {
        Set<String> ipAddresses = new HashSet<>();

        if(ipDataRepository.existsByAssetId(assetId)){
            ipDataRepository.deleteAllByAssetId(assetId);
        }
        for (SubdomainDataDetails dataDetails : subdomainDataDetailsList) {
            String subdomain = dataDetails.getSubdomain();
            System.out.println(subdomain);

            try {
                String ipAddress = assetEnumeration.DomainToIP(subdomain);
                if (ipAddresses.contains(ipAddress)) {
                    List<IpData> ipDataList = ipDataRepository.findByAssetId(assetId);
                    for (IpData ipData : ipDataList) {
                        if (ipData.getIpAddress().contains(ipAddress)) {
                            ipData.addShareSubdomains(subdomain);
                            ipDataRepository.save(ipData);
                            break;
                        }
                    }
                } else {
                    IpData ipData = new IpData(ipAddress, subdomain, assetId);
                    ipDataRepository.save(ipData);
                }
                ipAddresses.add(ipAddress);
            } catch (UnknownHostException e) {
                System.err.println("Failed to resolve IP address for domain: " + subdomain);
            } catch (IOException e) {
                System.err.println("An error occurred while resolving IP address for domain: " + subdomain);
                e.printStackTrace();
            }
        }
    }




}
