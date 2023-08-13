package com.beam.assetManagement.assetRecon.IpData;

import com.beam.assetManagement.assetRecon.SubdomainDataDetails.SubdomainDataDetails;
import com.beam.assetManagement.assets.Asset;
import com.beam.assetManagement.assets.AssetRepository;
import com.beam.assetManagement.emailSender.EmailSenderService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
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
@Slf4j
public class IpDataService {

    private final IpDataRepository ipDataRepository;

    private final AssetRepository assetRepository;

    private final EmailSenderService emailSenderService;


    public String DomainToIP(String domain) throws IOException, UnknownHostException {

        InetAddress ipAddress = InetAddress.getByName(domain);
        String ipAddressString = ipAddress.getHostAddress();
        return ipAddressString;

    }

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
            log.info(subdomain);

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

                    IpData ipData = IpData.builder().ipAddress(ipAddress).assetId(assetId).subdomainShareIp(new HashSet<>()).accessData(new ArrayList<>()).build();

                    ipData.addShareSubdomains(subdomain);
                    ipData.setId(UUID.randomUUID().toString());

                    ipDataRepository.save(ipData);
                }
                ipAddresses.add(ipAddress);
            } catch (UnknownHostException e) {
                log.info("Failed to resolve IP address for domain: " + subdomain);
            } catch (IOException e) {
                log.info("An error occurred while resolving IP address for domain: " + subdomain);
                e.printStackTrace();
            }
        }
    }

    public void insertPortScanToObject(String assetId, String firmId) throws IOException {

        List<IpData> ipDataList = ipDataRepository.findByAssetId(assetId);

        try {

            for (IpData obj : ipDataList) {
                String ipAddress = obj.getIpAddress();
                List<SubdomainPortData> portData = getPortData(ipAddress);
                obj.insertPortData(portData);
                obj.setFirmId(firmId);
                ipDataRepository.save(obj);

            }

        } catch (DataAccessException e) {
            log.error("Data access error occurred while processing port scan for assetId: " + assetId, e);
        }


    }


    public List<String> detectPort(String assetId) throws IOException, MessagingException {
        List<String> newPorts = new ArrayList<>();
        List<String> closedPorts = new ArrayList<>();

        List<IpData> ipDataList = ipDataRepository.findByAssetId(assetId);
        Optional<Asset> asset = assetRepository.findById(assetId);
        String assetName = asset.get().getAssetName();

        if (!ipDataList.isEmpty()) {
            for (IpData ipData : ipDataList) {
                String ipAddress = ipData.getIpAddress();
                List<SubdomainPortData> portDataList = getPortData(ipAddress);
                for (SubdomainPortData subdomainPortData : portDataList) {
                    String port = subdomainPortData.getPort();
                    boolean found = false;
                    boolean notExist = true;
                    for (SubdomainPortData existingData : ipData.getPortScanData()) {


                        if (existingData.getPort().equals(port)) {
                            found = true;
                            notExist = false;
                            break;
                        }
                    }
                    if (!found) {
                        log.info("MISSING PORT: " + port);
                        newPorts.add(port);
                        newPorts.add(ipAddress);
                    } else if (notExist) {
                        closedPorts.add(port);
                        closedPorts.add(ipAddress);
                    }
                }
            }
        } else {
            log.info("NO DATA FOUND");
        }
        if (!newPorts.isEmpty() || !closedPorts.isEmpty()) {
            emailSenderService.sendPortStatusEmail(newPorts, closedPorts, assetName);
        }
        return newPorts;
    }


    public List<IpData> getIpDataObjectList(String assetId) {
        return ipDataRepository.findByAssetId(assetId);
    }


}
