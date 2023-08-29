package com.beam.assetManagement.assetRecon.IpData;

import com.beam.assetManagement.assetRecon.SubdomainDataDetails.SubdomainDataDetails;
import com.beam.assetManagement.assets.Asset;
import com.beam.assetManagement.assets.AssetRepository;
import com.beam.assetManagement.emailSender.EmailSenderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
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
import java.util.stream.Collectors;

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

    public String shodanCityEnum(String ipAddress) throws JsonProcessingException {
        JsonNode jsonRoot = shodanIpEnum(ipAddress);
        JsonNode cityNode = jsonRoot.get("city");
        return getNodeValueAsString(cityNode);
    }

    public String shodanCountryCodeEnum(String ipAddress) throws JsonProcessingException {
        JsonNode jsonRoot = shodanIpEnum(ipAddress);
        JsonNode countryCodeNode = jsonRoot.get("country_code");
        return getNodeValueAsString(countryCodeNode);
    }

    public String shodanIspEnum(String ipAddress) throws JsonProcessingException {
        JsonNode jsonRoot = shodanIpEnum(ipAddress);
        JsonNode ispNode = jsonRoot.get("isp");
        return getNodeValueAsString(ispNode);
    }

    public String shodanRegionCodeEnum(String ipAddress) throws JsonProcessingException {
        JsonNode jsonRoot = shodanIpEnum(ipAddress);
        JsonNode regionCodeNode = jsonRoot.get("region_code");
        return getNodeValueAsString(regionCodeNode);
    }

    public double shodanLongitudeEnum(String ipAddress) throws JsonProcessingException {
        JsonNode jsonRoot = shodanIpEnum(ipAddress);
        JsonNode longitudeNode = jsonRoot.get("longitude");
        return getNodeValueAsDouble(longitudeNode);
    }

    public double shodanLatitudeEnum(String ipAddress) throws JsonProcessingException {
        JsonNode jsonRoot = shodanIpEnum(ipAddress);
        JsonNode latitudeNode = jsonRoot.get("latitude");
        return getNodeValueAsDouble(latitudeNode);
    }

    public String shodanOperatingSystemEnum(String ipAddress) throws JsonProcessingException {
        JsonNode jsonRoot = shodanIpEnum(ipAddress);
        JsonNode osNode = jsonRoot.at("/data/0/os");
        return getNodeValueAsString(osNode);
    }

    public Set<String> shodanVulnEnum(String ipAddress) throws JsonProcessingException {
        Set<String> vulns = new HashSet<>();
        JsonNode jsonRoot = shodanIpEnum(ipAddress);
        JsonNode vulnsNode = jsonRoot.at("/vulns");
        if (vulnsNode != null && vulnsNode.isArray()) {
            for (JsonNode vulnNode : vulnsNode) {
                vulns.add(vulnNode.asText());
            }
        }
        return vulns;
    }

    public String shodanAsnEnum(String ipAddress) throws JsonProcessingException {
        JsonNode jsonRoot = shodanIpEnum(ipAddress);
        JsonNode asnNode = jsonRoot.get("asn");

        if (asnNode != null && !asnNode.isNull()) {
            String asn = asnNode.asText();
            return asn;
        } else {
            return null;
        }
    }

    private String getNodeValueAsString(JsonNode node) {
        if (node != null && !node.isNull()) {
            return node.asText();
        }
        return null;
    }

    private double getNodeValueAsDouble(JsonNode node) {
        if (node != null && !node.isNull()) {
            return node.asDouble();
        }
        return 0.0;
    }


    public JsonNode shodanIpEnum(String ipAddress) {
        String apiKey = System.getenv("SHODAN_API_KEY");
        String apiUrl = "https://api.shodan.io/shodan/host/" + ipAddress + "?key=" + apiKey;

        ObjectMapper objectMapper = new ObjectMapper();

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(apiUrl);

            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                String jsonResponse = EntityUtils.toString(response.getEntity());
                JsonNode jsonRoot = objectMapper.readTree(jsonResponse);
                return jsonRoot;

            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }





    public List<SubdomainPortData> getPortData(String ipAddress) throws IOException {

        String regexPort = "(\\d+\\/\\w+)\\s+(\\w+)\\s+([\\w\\/-]+)\\s*([\\w\\/-]*)";

        Pattern patternPort = Pattern.compile(regexPort);

        ProcessBuilder processBuilder = new ProcessBuilder("nmap","-sS","-sV",ipAddress);
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
                String serviceVersion = matcherPort.group(4);
                SubdomainPortData subdomainPortData = new SubdomainPortData(port, state, service,serviceVersion);
                subdomainPortDataList.add(subdomainPortData);

            }
            if (line.isEmpty()) {
                break;
            }
        }
        return subdomainPortDataList;
    }

    public void InsertToIpData(String assetId, String ipAddress, String subdomain) throws JsonProcessingException {
        List<IpData> ipDataList = ipDataRepository.findByAssetId(assetId);

        String asn = shodanAsnEnum(ipAddress);
        String city = shodanCityEnum(ipAddress);
        String country_code = shodanCountryCodeEnum(ipAddress);
        String region_code = shodanRegionCodeEnum(ipAddress);
        double latitude = shodanLatitudeEnum(ipAddress);
        double longitude = shodanLongitudeEnum(ipAddress);
        String isp = shodanIspEnum(ipAddress);
        Set<String> vulns = shodanVulnEnum(ipAddress);
        String os = shodanOperatingSystemEnum(ipAddress);
        for (IpData ipData : ipDataList) {
            if (ipData.getIpAddress().contains(ipAddress)) {
                ipData.addShareSubdomains(subdomain);ipData.setVulns(vulns);
                ipData.setOs(os);ipData.setAsn(asn);
                ipData.setCity(city);ipData.setCountry_code(country_code);
                ipData.setRegion_code(region_code);ipData.setIsp(isp);
                ipData.setPortScanData(new ArrayList<>());ipData.setLatitude(latitude);
                ipData.setLongitude(longitude);
                ipDataRepository.save(ipData);
                break;
            }
        }

    }

    public void createIpData(String assetId, String ipAddress, String subdomain) throws JsonProcessingException {
        String asn = shodanAsnEnum(ipAddress);
        String city = shodanCityEnum(ipAddress);
        String country_code = shodanCountryCodeEnum(ipAddress);
        String region_code = shodanRegionCodeEnum(ipAddress);
        double latitude = shodanLatitudeEnum(ipAddress);
        double longitude = shodanLongitudeEnum(ipAddress);
        String isp = shodanIspEnum(ipAddress);
        Set<String> vulns = shodanVulnEnum(ipAddress);
        String os = shodanOperatingSystemEnum(ipAddress);
        IpData ipData = IpData.builder()
                .ipAddress(ipAddress).assetId(assetId)
                .subdomainShareIp(new HashSet<>()).accessData(new ArrayList<>())
                .asn(asn).city(city).country_code(country_code).region_code(region_code).isp(isp)
                .vulns(vulns).latitude(latitude).longitude(longitude).os(os)
                .PortScanData(new ArrayList<>())
                .build();
        ipData.addShareSubdomains(subdomain);
        ipData.setId(UUID.randomUUID().toString());
        ipDataRepository.save(ipData);
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
                    InsertToIpData(assetId,ipAddress,subdomain);
                } else {
                    createIpData(assetId,ipAddress,subdomain);
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
                obj.setScanDate(new java.sql.Timestamp(new Date().getTime()));
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

                List<SubdomainPortData> existingPortDataList = ipData.getPortScanData();
                List<String> existingPorts = existingPortDataList.stream()
                        .map(SubdomainPortData::getPort)
                        .collect(Collectors.toList());

                for (SubdomainPortData subdomainPortData : portDataList) {
                    String port = subdomainPortData.getPort();

                    if (!existingPorts.contains(port)) {
                        log.info("MISSING PORT: " + port);
                        newPorts.add(port);
                        newPorts.add(ipAddress);
                    }
                }

                for (SubdomainPortData existingData : existingPortDataList) {
                    String port = existingData.getPort();

                    if (!portDataList.stream().anyMatch(data -> data.getPort().equals(port))) {
                        log.info("CLOSED PORT: " + port);
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
