package com.beam.assetManagement.assetRecon.SubdomainDataDetails;

import com.beam.assetManagement.assetRecon.IpData.IpDataService;
import com.beam.assetManagement.assetRecon.IpData.SubdomainPortData;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubdomainDataDetailService {

    private final SubdomainRepository subdomainRepository;

    private final SubdomainDetailsRepository subdomainDetailsRepository;

    private final IpDataService ipDataService;

    public List<String> getSubdomains(String domainName, Set<String> uniqueSubdomains, Set<String> uniqueSubdomainIds)
            throws IOException {

        subdomainFinder(uniqueSubdomains,domainName);

        List<String> subdomains = new ArrayList<>();
        Set<SubdomainDataDetails> subdomainDataDetailsList = new HashSet<>();

        for (String subdomain : uniqueSubdomains) {
            List<SubdomainPortData> subdomainPortData = ipDataService.getPortData(subdomain);

            if (subdomainPortData.isEmpty()) {
                if (subdomainDetailsRepository.existsBySubdomain(subdomain)) {
                    HostDownAndExist(uniqueSubdomainIds, subdomain);
                } else {
                    HostDownAndNotExist(uniqueSubdomainIds, subdomain, subdomainDataDetailsList);
                }
            } else {
                String redirectDomain = checkSubdomainRedirect(subdomain);
                if (redirectDomain != null) {
                    String extractedRedirectDomain = ExtractRedirectedDomain(redirectDomain);
                    if (!redirectDomain.matches(".\\b" + domainName + "\\b.")) {
                        List<String> redirectedSubdomains = getSubdomains(extractedRedirectDomain, uniqueSubdomains,
                                uniqueSubdomainIds);
                        subdomains.addAll(redirectedSubdomains);
                    }
                    if (subdomainDetailsRepository.existsBySubdomain(subdomain)) {
                        HostUpAndExistRedirected(uniqueSubdomainIds, subdomain, redirectDomain);
                    } else {
                        HostUpAndNotExistRedirected(uniqueSubdomainIds, subdomain, redirectDomain,
                                subdomainDataDetailsList);
                    }
                } else {
                    subdomains.add(subdomain);
                    if (subdomainDetailsRepository.existsBySubdomain(subdomain)) {
                        HostUpAndExistNotRedirected(uniqueSubdomainIds, subdomain);
                    } else {
                        HostUpAndNotExistNotRedirected(uniqueSubdomainIds, subdomain,
                                subdomainDataDetailsList);
                    }
                }
            }
        }
        subdomainDetailsRepository.saveAll(subdomainDataDetailsList);
        return subdomains;
    }


    public void subdomainFinder(Set<String> uniqueSubdomains,String domain) throws IOException {
        amassSearch(uniqueSubdomains,domain);
        crtShSearch(uniqueSubdomains,domain);
        shodanSearch(uniqueSubdomains,domain);
    }


    public void shodanSearch(Set<String>uniqueSubDomains,String domain){
        String apiKey = System.getenv("SHODAN_API_KEY");
        String apiUrl = "https://api.shodan.io/dns/domain/" + domain + "?key=" + apiKey;
        List<String> webTechnologies = null;
        String asn = null;

        ObjectMapper objectMapper = new ObjectMapper();

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(apiUrl);

            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                String jsonResponse = EntityUtils.toString(response.getEntity());
                JsonNode jsonArray = objectMapper.readTree(jsonResponse);

                for (JsonNode jsonNode : jsonArray) {
                    asn = jsonNode.get("asn").asText();
                    JsonNode componentsNode = jsonNode.get("components");
                    if (componentsNode != null && componentsNode.isArray()) {
                        for (JsonNode componentNode : componentsNode) {
                            webTechnologies.add(componentNode.asText());
                            
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        log.info(webTechnologies.toString());
        log.info(asn);
    }



    public void crtShSearch(Set<String>uniqueSubDomains,String domain) {
        String apiUrl = "https://crt.sh/?q=%25." + domain + "&output=json";

        ObjectMapper objectMapper = new ObjectMapper();

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(apiUrl);

            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                String jsonResponse = EntityUtils.toString(response.getEntity());
                JsonNode jsonArray = objectMapper.readTree(jsonResponse);

                for (JsonNode jsonNode : jsonArray) {
                    String commonName = jsonNode.get("common_name").asText();
                    String nameValue = jsonNode.get("name_value").asText();
                    uniqueSubDomains.add(commonName);
                    uniqueSubDomains.add(nameValue);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void amassSearch(Set<String> uniqueSubdomains,String domain) throws IOException {
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

    public Set<SubdomainDataDetails> getDataDetailsObjectById(String assetId){

        Optional<SubdomainData> subdomainData= subdomainRepository.findById(assetId);
        SubdomainData data = subdomainData.get();
        Set<String> subdomainIds = data.getSubdomainIds();
        Set<SubdomainDataDetails> subdomainDataDetailsList = new HashSet<>();
        for(String stock: subdomainIds){
            Optional<SubdomainDataDetails> subdomainDataDetails = subdomainDetailsRepository.findById(stock);
            SubdomainDataDetails object=subdomainDataDetails.get();
            boolean isHostDown = object.isHostDown();
            if(!isHostDown){
                subdomainDataDetailsList.add(object);
            }
        }
        System.out.println(subdomainDataDetailsList.size());
        return subdomainDataDetailsList;
    }



    public void HostDownAndExist(Set<String> uniqueSubdomainIds, String line){

        SubdomainDataDetails savedDetails = findBySubdomain(line);
        savedDetails.setHostDown(true);

        String savedSubdomainId = savedDetails.getId();
        uniqueSubdomainIds.add(savedSubdomainId);
    }

    public void HostDownAndNotExist(Set<String> uniqueSubdomainIds,String line,
                                    Set<SubdomainDataDetails> subdomainDataDetailsList){

        //SubdomainDataDetails subdomainDataDetails = new SubdomainDataDetails(line, true);
        SubdomainDataDetails subdomainDataDetails = SubdomainDataDetails.builder().id(UUID.randomUUID().toString())
                .subdomain(line).isHostDown(true).build();
        String subdomainId = subdomainDataDetails.getId();
        uniqueSubdomainIds.add(subdomainId);
        subdomainDataDetailsList.add(subdomainDataDetails);

    }

    public void HostUpAndExistRedirected(Set<String> uniqueSubdomainIds,String line,
                                         String redirectDomain){

        SubdomainDataDetails savedDetails = findBySubdomain(line);
        savedDetails.setHostDown(false);

        savedDetails.setRedirectDomain(redirectDomain);
        savedDetails.setRedirected(true);
        String savedSubdomainId = savedDetails.getId();
        uniqueSubdomainIds.add(savedSubdomainId);
    }

    public void HostUpAndNotExistRedirected(Set<String> uniqueSubdomainIds,String line,
                                            String redirectDomain,
                                            Set<SubdomainDataDetails> subdomainDataDetailsList){

        //SubdomainDataDetails subdomainDataDetails = new SubdomainDataDetails(line,
        //        true, redirectDomain);
        SubdomainDataDetails subdomainDataDetails = SubdomainDataDetails.builder().id(UUID.randomUUID().toString())
                .subdomain(line).isRedirected(true).redirectDomain(redirectDomain).build();
        String subdomainId = subdomainDataDetails.getId();
        uniqueSubdomainIds.add(subdomainId);
        subdomainDataDetailsList.add(subdomainDataDetails);
    }

    public void HostUpAndExistNotRedirected(Set<String> uniqueSubdomainIds,String line){

        SubdomainDataDetails savedDetails = subdomainDetailsRepository.findBySubdomain(line)
                .orElse(null);

        savedDetails.setHostDown(false);


        String savedSubdomainId = savedDetails.getId();
        uniqueSubdomainIds.add(savedSubdomainId);

    }

    public void HostUpAndNotExistNotRedirected(Set<String> uniqueSubdomainIds,String line,
                                               Set<SubdomainDataDetails> subdomainDataDetailsList){

        //SubdomainDataDetails subdomainDataDetails = new SubdomainDataDetails(line);
        SubdomainDataDetails subdomainDataDetails = SubdomainDataDetails.builder().id(UUID.randomUUID().toString())
                .subdomain(line).build();

        String subdomainId = subdomainDataDetails.getId();
        uniqueSubdomainIds.add(subdomainId);
        subdomainDataDetailsList.add(subdomainDataDetails);
    }

    public SubdomainDataDetails findBySubdomain(String line){
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

    public String ExtractRedirectedDomain(String redirectDomain){
        String extractedRedirectDomain = redirectDomain.replace("https://", "").
                replace("http://", "").replaceAll("/+$", "");
        return extractedRedirectDomain;
    }

}
