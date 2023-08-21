package com.beam.assetManagement.assetRecon.SubdomainDataDetails;

import com.beam.assetManagement.assetRecon.IpData.IpDataService;
import com.beam.assetManagement.assetRecon.SubdomainData.SubdomainData;
import com.beam.assetManagement.assetRecon.SubdomainData.SubdomainRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubdomainDataDetailService {

    private final SubdomainRepository subdomainRepository;

    private final SubdomainDetailsRepository subdomainDetailsRepository;

    private final IpDataService ipDataService;


    public List<String> getSubdomains(String domainName, Set<String> uniqueSubdomainIds) throws IOException {
        Set<String> uniqueSubdomains = subdomainFinder(domainName);
        List<String> subdomains = new ArrayList<>();
        Set<SubdomainDataDetails> subdomainDataDetailsList = new HashSet<>();
        for (String subdomain : uniqueSubdomains) {
            String redirectDomain = checkSubdomainRedirect(subdomain);
            String extractedRedirectDomain = ExtractRedirectedDomain(redirectDomain);
            if (redirectDomain != null) {
                if (!redirectDomain.matches(".*\\b" + domainName + "\\b.*")) {
                    List<String> redirectedSubdomains = getSubdomains(extractedRedirectDomain, uniqueSubdomainIds);
                    subdomains.addAll(redirectedSubdomains);
                }
            }
            if (!isSubdomainUp(subdomain)) {
                if (subdomainDetailsRepository.existsBySubdomain(subdomain)) {
                    HostDownAndExist(uniqueSubdomainIds, subdomain);
                } else {
                    HostDownAndNotExist(uniqueSubdomainIds, subdomain, subdomainDataDetailsList);
                }
            } else {
                boolean isSSLValid = isSSLValid(subdomain);
                if (redirectDomain != null) {
                    if (subdomainDetailsRepository.existsBySubdomain(subdomain)) {
                        HostUpAndExistRedirected(uniqueSubdomainIds, subdomain, redirectDomain, isSSLValid);
                    } else {
                        HostUpAndNotExistRedirected(uniqueSubdomainIds, subdomain, redirectDomain, subdomainDataDetailsList, isSSLValid);
                    }
                } else {
                    subdomains.add(subdomain);
                    if (subdomainDetailsRepository.existsBySubdomain(subdomain)) {
                        HostUpAndExistNotRedirected(uniqueSubdomainIds, subdomain, isSSLValid);
                    } else {
                        HostUpAndNotExistNotRedirected(uniqueSubdomainIds, subdomain, subdomainDataDetailsList, isSSLValid);
                    }
                }
            }
        }
        subdomainDetailsRepository.saveAll(subdomainDataDetailsList);
        return subdomains;
    }

    public static boolean isSubdomainUp(String subdomain) {
        try {
            Process process = Runtime.getRuntime().exec("ping -c 1 " + subdomain);
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (IOException | InterruptedException e) {
            return false;
        }
    }


    public Set<String> subdomainFinder(String domain) throws IOException {
        Set<String> uniqueSubdomains = new HashSet<>();
        amassSearch(uniqueSubdomains, domain);
        crtShSearch(uniqueSubdomains, domain);
        shodanSearch(uniqueSubdomains, domain);
        return uniqueSubdomains;
    }


    public void shodanSearch(Set<String> uniqueSubDomains, String domain) {
        String apiKey = System.getenv("SHODAN_API_KEY");
        String apiUrl = "https://api.shodan.io/dns/domain/" + domain + "?key=" + apiKey;

        ObjectMapper objectMapper = new ObjectMapper();

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(apiUrl);

            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                String jsonResponse = EntityUtils.toString(response.getEntity());
                JsonNode jsonRoot = objectMapper.readTree(jsonResponse);

                JsonNode subdomainsNode = jsonRoot.get("subdomains");
                if (subdomainsNode != null && subdomainsNode.isArray()) {
                    for (JsonNode subdomainNode : subdomainsNode) {
                        String subdomain = subdomainNode.asText() + "." + domain;
                        uniqueSubDomains.add(subdomain);
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        log.info(uniqueSubDomains.toString());
    }


    public boolean isSSLValid(String domain) {
        try {
            SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            SSLSocket sslSocket = (SSLSocket) sslSocketFactory.createSocket(domain, 443);
            sslSocket.startHandshake();
            SSLSession sslSession = sslSocket.getSession();
            Certificate[] certificates = sslSession.getPeerCertificates();

            if (certificates.length > 0 && certificates[0] instanceof X509Certificate) {
                X509Certificate x509Certificate = (X509Certificate) certificates[0];
                return x509Certificate.getNotAfter().compareTo(new java.util.Date()) > 0;
            } else {
                return false;
            }
        } catch (IOException e) {
            return false;
        }
    }


    public void crtShSearch(Set<String> uniqueSubDomains, String domain) {
        String apiUrl = "https://crt.sh/?q=%25." + domain + "&output=json";

        ObjectMapper objectMapper = new ObjectMapper();

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(apiUrl);

            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                String jsonResponse = EntityUtils.toString(response.getEntity());
                JsonNode jsonArray = objectMapper.readTree(jsonResponse);

                for (JsonNode jsonNode : jsonArray) {
                    JsonNode commonNameNode = jsonNode.get("common_name");
                    if (commonNameNode != null && !commonNameNode.isNull()) {
                        String commonName = commonNameNode.asText();
                        if (commonName.startsWith("*.")) {
                            commonName.substring(2);
                        }
                        uniqueSubDomains.add(commonName);
                    }

                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void amassSearch(Set<String> uniqueSubdomains, String domain) throws IOException {
        String amassPath = "C:\\amass_Windows_amd64\\amass.exe";

        ProcessBuilder processBuilder = new ProcessBuilder(amassPath, "enum", "-passive", "-d", domain);
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) {
                break;
            }
            uniqueSubdomains.add(line);
        }
    }

    public Set<SubdomainDataDetails> getDataDetailsObjectById(String assetId) {

        Optional<SubdomainData> subdomainData = subdomainRepository.findById(assetId);
        SubdomainData data = subdomainData.get();
        Set<String> subdomainIds = data.getSubdomainIds();
        Set<SubdomainDataDetails> subdomainDataDetailsList = new HashSet<>();
        for (String stock : subdomainIds) {
            Optional<SubdomainDataDetails> subdomainDataDetails = subdomainDetailsRepository.findById(stock);
            SubdomainDataDetails object = subdomainDataDetails.get();
            boolean isHostDown = object.isHostDown();
            if (!isHostDown) {
                subdomainDataDetailsList.add(object);
            }
        }

        return subdomainDataDetailsList;
    }


    public void HostDownAndExist(Set<String> uniqueSubdomainIds, String line) {

        SubdomainDataDetails savedDetails = findBySubdomain(line);
        savedDetails.setHostDown(true);

        String savedSubdomainId = savedDetails.getId();
        uniqueSubdomainIds.add(savedSubdomainId);
    }

    public void HostDownAndNotExist(Set<String> uniqueSubdomainIds, String line, Set<SubdomainDataDetails> subdomainDataDetailsList) {

        //SubdomainDataDetails subdomainDataDetails = new SubdomainDataDetails(line, true);
        SubdomainDataDetails subdomainDataDetails = SubdomainDataDetails.builder().id(UUID.randomUUID().toString()).subdomain(line).isHostDown(true).build();
        String subdomainId = subdomainDataDetails.getId();
        uniqueSubdomainIds.add(subdomainId);
        subdomainDataDetailsList.add(subdomainDataDetails);

    }

    public void HostUpAndExistRedirected(Set<String> uniqueSubdomainIds, String line, String redirectDomain, boolean isSSLValid) {

        SubdomainDataDetails savedDetails = findBySubdomain(line);
        savedDetails.setHostDown(false);
        savedDetails.setSSLValid(isSSLValid);
        savedDetails.setRedirectDomain(redirectDomain);
        savedDetails.setRedirected(true);
        String savedSubdomainId = savedDetails.getId();
        uniqueSubdomainIds.add(savedSubdomainId);
        subdomainDetailsRepository.save(savedDetails);
    }

    public void HostUpAndNotExistRedirected(Set<String> uniqueSubdomainIds, String line, String redirectDomain, Set<SubdomainDataDetails> subdomainDataDetailsList, boolean isSSLValid) {

        //SubdomainDataDetails subdomainDataDetails = new SubdomainDataDetails(line,
        //        true, redirectDomain);
        SubdomainDataDetails subdomainDataDetails = SubdomainDataDetails.builder().id(UUID.randomUUID().toString()).subdomain(line).isRedirected(true).redirectDomain(redirectDomain).isSSLValid(isSSLValid).build();
        String subdomainId = subdomainDataDetails.getId();

        uniqueSubdomainIds.add(subdomainId);
        subdomainDataDetailsList.add(subdomainDataDetails);
    }

    public void HostUpAndExistNotRedirected(Set<String> uniqueSubdomainIds, String line, boolean isSSLValid) {

        SubdomainDataDetails savedDetails = subdomainDetailsRepository.findBySubdomain(line).orElse(null);

        savedDetails.setHostDown(false);

        savedDetails.setSSLValid(isSSLValid);
        String savedSubdomainId = savedDetails.getId();
        uniqueSubdomainIds.add(savedSubdomainId);
        subdomainDetailsRepository.save(savedDetails);


    }

    public void HostUpAndNotExistNotRedirected(Set<String> uniqueSubdomainIds, String line, Set<SubdomainDataDetails> subdomainDataDetailsList, boolean isSSLValid) {

        //SubdomainDataDetails subdomainDataDetails = new SubdomainDataDetails(line);
        SubdomainDataDetails subdomainDataDetails = SubdomainDataDetails.builder().id(UUID.randomUUID().toString()).subdomain(line).isSSLValid(isSSLValid).build();

        String subdomainId = subdomainDataDetails.getId();
        uniqueSubdomainIds.add(subdomainId);
        subdomainDataDetailsList.add(subdomainDataDetails);
    }

    public SubdomainDataDetails findBySubdomain(String line) {
        SubdomainDataDetails savedDetails = subdomainDetailsRepository.findBySubdomain(line).orElse(null);
        return savedDetails;

    }


    public String checkSubdomainRedirect(String subdomain) throws IOException {
        String command = "curl -sIL " + subdomain + " | grep -i 'location: '";
        Process process = Runtime.getRuntime().exec(command);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.toLowerCase().startsWith("location: ")) {
                    return line.substring("location: ".length()).trim(); // Extract the redirect domain
                }
            }
        }
        return null;
    }

    public String ExtractRedirectedDomain(String redirectDomain) {
        if (redirectDomain == null) {
            return null;
        }
        String extractedRedirectDomain = redirectDomain.replace("https://", "").replace("http://", "").replaceAll("/+$", "");
        return extractedRedirectDomain;
    }


}
