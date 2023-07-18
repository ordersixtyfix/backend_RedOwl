package com.beam.assetManagement.assetRecon;

        import com.beam.assetManagement.assets.Asset;
        import com.beam.assetManagement.assets.AssetData;
        import com.beam.assetManagement.assets.AssetRepository;
        import com.beam.assetManagement.utils.RegexMatcherService;
        import org.apache.commons.net.whois.WhoisClient;

        import lombok.AllArgsConstructor;
        import org.springframework.stereotype.Service;

        import java.io.BufferedReader;
        import java.io.IOException;
        import java.io.InputStreamReader;
        import java.io.Reader;
        import java.net.InetAddress;
        import java.net.UnknownHostException;
        import java.util.*;
        import java.util.regex.Matcher;
        import java.util.regex.Pattern;


@Service
@AllArgsConstructor

public class AssetEnumeration {

    private final AssetRepository assetRepository;

    private final SubdomainRepository subdomainRepository;

    private final SubdomainDetailsRepository subdomainDetailsRepository;

    private final IpDataRepository ipDataRepository;

    private final RegexMatcherService regexMatcherService;

    static String assetDomainName;




    public List<String> setAsset(Optional<Asset> asset) throws Exception {

        Asset testAsset = asset.get();
        String assetDomain = testAsset.getAssetDomain();
        String assetName = testAsset.getAssetName();

        assetDomainName = assetName;

        String assetId = testAsset.getAssetId();
        Asset savedAsset = assetRepository.findByAssetId(testAsset.getAssetId()).orElse(null);
        String modifiedDomain = assetDomain.substring(4);

        List<String> nameServers = getNameServers(modifiedDomain);
        List<String> registrarServer = getRegistrarData(modifiedDomain);
        Set<String> uniqueSubdomains = new HashSet<>();
        Set<String> uniqueSubdomainIds = new HashSet<>();
        //Set<String> uniqueIpAddresses = new HashSet<>();


        getSubdomains(modifiedDomain,uniqueSubdomains,uniqueSubdomainIds);
        SaveSubdomainData(uniqueSubdomainIds,assetId);

        //getDataDetailsObjectById(assetId);
        getIpFromDataDetailsObject(getDataDetailsObjectById(assetId),assetId);
        insertPortScanToObject(assetId);


        AssetData assetData = new AssetData(registrarServer, nameServers);
        savedAsset.setAssetData(assetData);

        String add="null";


        String adsd="null";

        assetRepository.save(savedAsset);
        return null;

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
        System.out.println(subdomainDataDetailsList.size());
        return subdomainDataDetailsList;
    }


    public void insertPortScanToObject(String assetId) throws IOException {

        List<IpData> ipDataList = ipDataRepository.findByAssetId(assetId);

        for(IpData obj : ipDataList){
            String ipAddress = obj.getIpAddress();
            List<SubdomainPortData> portData=getPortData(ipAddress);
            obj.insertPortData(portData);
            ipDataRepository.save(obj);

        }

    }






    public void getIpFromDataDetailsObject(Set<SubdomainDataDetails> subdomainDataDetailsList,String assetId)
            throws IOException {
        Set<String> ipAddresses = new HashSet<>();

        if(ipDataRepository.existsByAssetId(assetId)){
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

    public String DomainToIP(String domain) throws IOException, UnknownHostException{

        InetAddress ipAddress = InetAddress.getByName(domain);
        String ipAddressString = ipAddress.getHostAddress();
        return ipAddressString;

    }




    public static List<String> getNameServers(String domainName) throws Exception {
        WhoisClient whoisClient = new WhoisClient();
        whoisClient.connect(WhoisClient.DEFAULT_HOST);
        String whoisResponse = whoisClient.query(domainName);
        System.out.println(whoisResponse);
        whoisClient.disconnect();

        String pattern = "Name Server:\\s*(.*)";
        Pattern regex = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
        Matcher matcher = regex.matcher(whoisResponse);

        List<String> nameServers = new ArrayList<>();

        while(matcher.find()){
            String nameServer = matcher.group();
            String trimmedNameServer = trimBeforeColon(nameServer);
            nameServers.add(trimmedNameServer);
        }

        System.out.println(nameServers);

        return nameServers;
    }



    public List<String> getRegistrarData(String domainName) throws Exception {
        WhoisClient whoisClient = new WhoisClient();
        whoisClient.connect(WhoisClient.DEFAULT_HOST);
        String whoisResponse = whoisClient.query(domainName);
        System.out.println(whoisResponse);
        whoisClient.disconnect();

        List<String> registrarData = regexMatcherService.checkRegistrarData(whoisResponse);

        return registrarData;
    }




    public List<String> getSubdomains(String domainName, Set<String> uniqueSubdomains,Set<String> uniqueSubdomainIds)
            throws IOException{
        Reader reader = AmassCommandOutput(domainName);
        List<String> subdomains = new ArrayList<>();
        Set<SubdomainDataDetails> subdomainDataDetailsList = new HashSet<>();
        String line;
        while ((line = ((BufferedReader) reader).readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) {
                break;
            }
            List<SubdomainPortData> subdomainPortData = getPortData(line);
            if (!uniqueSubdomains.contains(line)) {
                if (subdomainPortData.isEmpty()) {
                    if(subdomainDetailsRepository.existsBySubdomain(line)){
                        HostDownAndExist(uniqueSubdomainIds,line,subdomainPortData);
                    } else {
                        HostDownAndNotExist(uniqueSubdomainIds,line,subdomainDataDetailsList);
                    }
                } else {
                    String redirectDomain = checkSubdomainRedirect(line);
                    if (redirectDomain != null) {
                        String extractedRedirectDomain = ExtractRedirectedDomain(redirectDomain);
                        if (!redirectDomain.matches(".*\\b" + domainName + "\\b.*")) {
                            List<String> redirectedSubdomains = getSubdomains(extractedRedirectDomain, uniqueSubdomains,
                                    uniqueSubdomainIds);
                            subdomains.addAll(redirectedSubdomains);
                        }
                        if(subdomainDetailsRepository.existsBySubdomain(line)){
                            HostUpAndExistRedirected(uniqueSubdomainIds,line,subdomainPortData,redirectDomain);
                        }
                        else {
                            HostUpAndNotExistRedirected(uniqueSubdomainIds,line,subdomainPortData,redirectDomain,
                                    subdomainDataDetailsList);
                        }
                    } else {
                        subdomains.add(line);
                        if(subdomainDetailsRepository.existsBySubdomain(line)){
                            HostUpAndExistNotRedirected(uniqueSubdomainIds,line,subdomainPortData);
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

    public String ExtractRedirectedDomain(String redirectDomain){
        String extractedRedirectDomain = redirectDomain.replace("https://", "").
                replace("http://", "").replaceAll("/+$", "");
        return extractedRedirectDomain;
    }




    public BufferedReader AmassCommandOutput(String domain) throws IOException {
        String amassPath = "C:\\amass_Windows_amd64\\amass.exe";

        ProcessBuilder processBuilder = new ProcessBuilder(amassPath, "enum", "-passive", "-d", domain);
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

        return  reader;


    }
    public void SaveSubdomainData(Set<String> subdomainIds, String assetId) {

        Set<String> subdomainIdList = subdomainIds;
        SubdomainData subdomainData = new SubdomainData(subdomainIdList,assetDomainName,assetId);
        subdomainRepository.save(subdomainData);

    }

    public void HostDownAndExist(Set<String> uniqueSubdomainIds, String line,
                                   List<SubdomainPortData> subdomainPortData){

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

    public void HostUpAndExistRedirected(Set<String> uniqueSubdomainIds,String line,List<SubdomainPortData> subdomainPortData,
                                 String redirectDomain){

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

    public void HostUpAndExistNotRedirected(Set<String> uniqueSubdomainIds,String line,
                                            List<SubdomainPortData> subdomainPortData){

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

    public List<SubdomainPortData> getPortData(String subdomain) throws IOException{

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

    public static String trimBeforeColon(String input) {
        int colonIndex = input.indexOf(":");
        if (colonIndex != -1) {
            return input.substring(colonIndex + 1).trim();
        } else {
            return input;
        }
    }




    public void TryPortServiceAccess(List<IpData> ipData) throws IOException {

        for(IpData obj : ipData){
            String ipAddress = obj.getIpAddress();


            List<SubdomainPortData> data = obj.getPortScanData();


            for(SubdomainPortData portObj: data){
                String portService = portObj.getPortService();
                String portWithProtocol = portObj.getPort();
                String onlyPort = removeProtocol(portWithProtocol);

                String exampleOutput = "Nap scan report for 192.168.1.150\n" +
                        "Host is up (0.00018s latency).\n" +
                        "PORT\n" +
                        "STATE SERVICE\n" +
                        "22/tcp open ssh ssh-brute:\n" +
                        "Accounts:\n" +
                        "msfadmin:msfadmin - Valid credentials postgres:postgres - Valid credentials\n" +
                        "Statistics:\n" +
                        "Pertormed 73 guesses in\n" +
                        "42 seconds, average tps: 1.8\n" +
                        "MAC Address: 00: 0C: 29:77: BA: E7 (VMware)";



                String output = bruteForcePortService(ipAddress, portService, onlyPort);
                String extractedPart = extractOutputPart(exampleOutput);

                System.out.println("Extracted Part:\n" + extractedPart);
            }
        }

    }

    public static String bruteForcePortService(String ipAddress, String portService, String port) throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder("nmap", "--script", portService + "-brute", "-p" + port, ipAddress, "-T3");
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

        StringBuilder outputBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {

            outputBuilder.append(line).append("\n"); // Store the output for further processing
        }

        return outputBuilder.toString();
    }




    public static String extractOutputPart(String output) {

        String patternString = "\\b([a-zA-Z0-9]+:[a-zA-Z0-9]+) - Valid credentials\\b";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(output);

        StringBuilder matchedStrings = new StringBuilder();




        while (matcher.find()) {
            String result = matcher.group(1);
            matchedStrings.append(result).append(" ");
        }

        String finalResult = matchedStrings.toString().trim();

        return finalResult;

    }


    public static String removeProtocol(String input) {
        Pattern pattern = Pattern.compile("\\/\\w+$");
        Matcher matcher = pattern.matcher(input);
        return matcher.replaceAll("");
    }



}







