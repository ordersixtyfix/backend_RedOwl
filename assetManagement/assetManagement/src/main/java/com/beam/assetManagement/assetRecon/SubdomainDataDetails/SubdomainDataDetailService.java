package com.beam.assetManagement.assetRecon.SubdomainDataDetails;

import com.beam.assetManagement.assetRecon.AssetEnumeration;
import com.beam.assetManagement.assetRecon.IpData.SubdomainPortData;
import com.beam.assetManagement.assetRecon.SubdomainData.SubdomainData;
import com.beam.assetManagement.assetRecon.SubdomainData.SubdomainRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;

@Service
@AllArgsConstructor
public class SubdomainDataDetailService {

    @Autowired
    private SubdomainRepository subdomainRepository;
    @Autowired
    private SubdomainDetailsRepository subdomainDetailsRepository;

    private AssetEnumeration assetEnumeration;

    @Autowired
    public SubdomainDataDetailService(@Lazy AssetEnumeration assetEnumeration){
        this.assetEnumeration = assetEnumeration;
    }




    public List<String> getSubdomains(String domainName, Set<String> uniqueSubdomains, Set<String> uniqueSubdomainIds)
            throws IOException {
        Reader reader = AmassCommandOutput(domainName);
        List<String> subdomains = new ArrayList<>();
        Set<SubdomainDataDetails> subdomainDataDetailsList = new HashSet<>();
        String line;
        while ((line = ((BufferedReader) reader).readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) {
                break;
            }
            List<SubdomainPortData> subdomainPortData = assetEnumeration.getPortData(line);
            if (!uniqueSubdomains.contains(line)) {
                if (subdomainPortData.isEmpty()) {
                    if(subdomainDetailsRepository.existsBySubdomain(line)){
                        HostDownAndExist(uniqueSubdomainIds,line);
                    } else {
                        HostDownAndNotExist(uniqueSubdomainIds,line,subdomainDataDetailsList);
                    }
                } else {
                    String redirectDomain = checkSubdomainRedirect(line);
                    if (redirectDomain != null) {
                        String extractedRedirectDomain = extractRedirectedDomain(redirectDomain);
                        if (!redirectDomain.matches(".*\\b" + domainName + "\\b.*")) {
                            List<String> redirectedSubdomains = getSubdomains(extractedRedirectDomain, uniqueSubdomains,
                                    uniqueSubdomainIds);
                            subdomains.addAll(redirectedSubdomains);
                        }
                        if(subdomainDetailsRepository.existsBySubdomain(line)){
                            HostUpAndExistRedirected(uniqueSubdomainIds,line,redirectDomain);
                        }
                        else {
                            HostUpAndNotExistRedirected(uniqueSubdomainIds,line,subdomainPortData,redirectDomain,
                                    subdomainDataDetailsList);
                        }
                    } else {
                        subdomains.add(line);
                        if(subdomainDetailsRepository.existsBySubdomain(line)){
                            HostUpAndExistNotRedirected(uniqueSubdomainIds,line);
                        }
                        else {
                            HostUpAndNotExistNotRedirected(uniqueSubdomainIds,line,subdomainPortData,
                                    subdomainDataDetailsList);

                        }
                    }
                }
            }
        }
        subdomainDetailsRepository.saveAll(subdomainDataDetailsList);
        return subdomains;
    }


    public BufferedReader AmassCommandOutput(String domain) throws IOException {
        String amassPath = "C:\\amass_Windows_amd64\\amass.exe";

        ProcessBuilder processBuilder = new ProcessBuilder(amassPath, "enum", "-passive", "-d", domain);
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

        return  reader;


    }

    public Set<SubdomainDataDetails> getDataDetailsObjectById(String assetId){

        Optional<SubdomainData> subdomainData= subdomainRepository.findByDomainId(assetId);
        SubdomainData data = subdomainData.get();
        Set<String> subdomainIds = data.getSubdomainIds();
        Set<SubdomainDataDetails> subdomainDataDetailsList = new HashSet<>();
        for(String stock: subdomainIds){
            Optional<SubdomainDataDetails> subdomainDataDetails = subdomainDetailsRepository.findBySubdomainId(stock);
            SubdomainDataDetails object=subdomainDataDetails.get();
            boolean isHostDown = object.getHostDown();
            if(!isHostDown){
                subdomainDataDetailsList.add(object);
            }
        }

        return subdomainDataDetailsList;
    }


    public void HostDownAndExist(Set<String> uniqueSubdomainIds, String line){

        SubdomainDataDetails savedDetails = findBySubdomain(line);
        savedDetails.setHostDown(true);

        String savedSubdomainId = savedDetails.getSubdomainId();
        uniqueSubdomainIds.add(savedSubdomainId);
    }

    public void HostDownAndNotExist(Set<String> uniqueSubdomainIds,String line,
                                    Set<SubdomainDataDetails> subdomainDataDetailsList){

        SubdomainDataDetails subdomainDataDetails = new SubdomainDataDetails(line, true);
        String subdomainId = subdomainDataDetails.getSubdomainId();
        uniqueSubdomainIds.add(subdomainId);
        subdomainDataDetailsList.add(subdomainDataDetails);

    }

    public void HostUpAndExistRedirected(Set<String> uniqueSubdomainIds,String line, String redirectDomain){

        SubdomainDataDetails savedDetails = findBySubdomain(line);
        savedDetails.setHostDown(false);

        savedDetails.setRedirectDomain(redirectDomain);
        savedDetails.setRedirected(true);
        String savedSubdomainId = savedDetails.getSubdomainId();
        uniqueSubdomainIds.add(savedSubdomainId);
    }

    public void HostUpAndNotExistRedirected(Set<String> uniqueSubdomainIds,String line,List<SubdomainPortData> subdomainPortData,
                                            String redirectDomain,
                                            Set<SubdomainDataDetails> subdomainDataDetailsList){

        SubdomainDataDetails subdomainDataDetails = new SubdomainDataDetails(line, subdomainPortData,
                true, redirectDomain);
        String subdomainId = subdomainDataDetails.getSubdomainId();
        uniqueSubdomainIds.add(subdomainId);
        subdomainDataDetailsList.add(subdomainDataDetails);
    }

    public void HostUpAndExistNotRedirected(Set<String> uniqueSubdomainIds,String line){

        SubdomainDataDetails savedDetails = subdomainDetailsRepository.findBySubdomain(line)
                .orElse(null);

        savedDetails.setHostDown(false);


        String savedSubdomainId = savedDetails.getSubdomainId();
        uniqueSubdomainIds.add(savedSubdomainId);

    }

    public void HostUpAndNotExistNotRedirected(Set<String> uniqueSubdomainIds,String line,List<SubdomainPortData> subdomainPortData,
                                               Set<SubdomainDataDetails> subdomainDataDetailsList){

        SubdomainDataDetails subdomainDataDetails = new SubdomainDataDetails(line, subdomainPortData);

        String subdomainId = subdomainDataDetails.getSubdomainId();
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

    public String extractRedirectedDomain(String redirectDomain){
        String extractedRedirectDomain = redirectDomain.replace("https://", "").
                replace("http://", "").replaceAll("/+$", "");
        return extractedRedirectDomain;
    }










}
