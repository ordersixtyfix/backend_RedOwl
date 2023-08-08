package com.beam.assetManagement.assetRecon.IpData;

import com.beam.assetManagement.assetRecon.SubdomainDataDetails.SubdomainDataDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class IpDataService {

    private final IpDataRepository ipDataRepository;

    public String DomainToIP(String domain) throws IOException, UnknownHostException {

        InetAddress ipAddress = InetAddress.getByName(domain);
        String ipAddressString = ipAddress.getHostAddress();
        return ipAddressString;

    }
// burda processBuilder yerine kütüphane kullan
    public List<SubdomainPortData> getPortData(String subdomain) throws IOException {

        String regexPort = "(\\d+\\/\\w+)\\s+(\\w+)\\s+([\\w\\/-]+)";

        Pattern patternPort = Pattern.compile(regexPort);

        ProcessBuilder processBuilder = new ProcessBuilder("nmap", subdomain);
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        List<SubdomainPortData> subdomainPortDataList = new ArrayList<>();

        while ((line = reader.readLine()) != null) {
            line = line.trim();

            Matcher matcherPort = patternPort.matcher(line);

            if (matcherPort.matches()) {
                String port = matcherPort.group(1);
                String state = matcherPort.group(2);
                String service = matcherPort.group(3);
                SubdomainPortData subdomainPortData = new SubdomainPortData(port, state, service);
                subdomainPortDataList.add(subdomainPortData);

            }
            if (line.isEmpty()) {
                break;
            }
        }
        return subdomainPortDataList;
    }

    public void getIpFromDataDetailsObject(Set<SubdomainDataDetails> subdomainDataDetailsList, String assetId) throws IOException {
        Set<String> ipAddresses = new HashSet<>();

        if (ipDataRepository.existsByAssetId(assetId)) {
            ipDataRepository.deleteAllByAssetId(assetId);
        }
        for (SubdomainDataDetails dataDetails : subdomainDataDetailsList) {
            String subdomain = dataDetails.getSubdomain();
            System.out.println(subdomain);

            try {
                String ipAddress = DomainToIP(subdomain);
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

                    IpData ipData = IpData.builder().ipAddress(ipAddress).assetId(assetId)
                            .subdomainShareIp(new HashSet<>()).accessData(new ArrayList<>()).build();

                    ipData.addShareSubdomains(subdomain);
                    ipData.setId(UUID.randomUUID().toString());

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

    public void insertPortScanToObject(String assetId) throws IOException {

        List<IpData> ipDataList = ipDataRepository.findByAssetId(assetId);

        for (IpData obj : ipDataList) {
            String ipAddress = obj.getIpAddress();
            List<SubdomainPortData> portData = getPortData(ipAddress);
            obj.insertPortData(portData);
            ipDataRepository.save(obj);

        }

    }

    public List<IpData> getIpDataObjectList(String assetId) {
        return ipDataRepository.findByAssetId(assetId);
    }


}
