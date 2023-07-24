    package com.beam.assetManagement.assetRecon;

            import com.beam.assetManagement.assetRecon.IpData.IpData;
            import com.beam.assetManagement.assetRecon.IpData.IpDataService;
            import com.beam.assetManagement.assetRecon.IpData.SubdomainPortData;
            import com.beam.assetManagement.assetRecon.ServiceEnum.ServiceEnum;
            import com.beam.assetManagement.assetRecon.SubdomainData.SubdomainDataService;
            import com.beam.assetManagement.assetRecon.SubdomainDataDetails.SubdomainDataDetailService;
            import com.beam.assetManagement.assets.Asset;
            import com.beam.assetManagement.assets.AssetData;
            import com.beam.assetManagement.assets.AssetRepository;
            import com.beam.assetManagement.utils.RegexMatcherService;
            import org.apache.commons.net.whois.WhoisClient;

            import lombok.AllArgsConstructor;
            import org.springframework.stereotype.Component;

            import java.io.BufferedReader;
            import java.io.IOException;
            import java.io.InputStreamReader;
            import java.net.InetAddress;
            import java.net.UnknownHostException;
            import java.util.*;
            import java.util.regex.Matcher;
            import java.util.regex.Pattern;


    @Component
    @AllArgsConstructor


    public class AssetEnumeration {

        private final AssetRepository assetRepository;

        private final RegexMatcherService regexMatcherService;

        public static String assetDomainName;

        private final SubdomainDataService subdomainDataService;

        private final SubdomainDataDetailService subdomainDataDetailService;

        private final IpDataService ipDataService;

        private final ServiceEnum serviceEnum;




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


            subdomainDataDetailService.getSubdomains(modifiedDomain,uniqueSubdomains,uniqueSubdomainIds);
            subdomainDataService.SaveSubdomainData(uniqueSubdomainIds,assetId);

            //getDataDetailsObjectById(assetId);
            ipDataService.getIpFromDataDetailsObject(subdomainDataDetailService.getDataDetailsObjectById(assetId),assetId);
            ipDataService.insertPortScanToObject(assetId);


            AssetData assetData = new AssetData(registrarServer, nameServers);
            savedAsset.setAssetData(assetData);




            assetRepository.save(savedAsset);
            return null;

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






        public String ExtractRedirectedDomain(String redirectDomain){
            String extractedRedirectDomain = redirectDomain.replace("https://", "").
                    replace("http://", "").replaceAll("/+$", "");
            return extractedRedirectDomain;
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
                boolean accessibleData=false;
                if (matcherPort.matches()) {
                    String port = matcherPort.group(1);
                    String state = matcherPort.group(2);
                    String service = matcherPort.group(3);
                    serviceEnum.testConnection(service);
                    SubdomainPortData subdomainPortData = new SubdomainPortData(port, state, service,accessibleData);
                    subdomainPortDataList.add(subdomainPortData);

                }
                if (line.isEmpty()) {
                    break;
                }
            }
            return subdomainPortDataList;
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







