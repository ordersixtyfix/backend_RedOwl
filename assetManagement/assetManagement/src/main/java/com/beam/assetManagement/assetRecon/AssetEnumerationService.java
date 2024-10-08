    package com.beam.assetManagement.assetRecon;

    import com.beam.assetManagement.assetRecon.IpData.IpData;
    import com.beam.assetManagement.assetRecon.IpData.IpDataService;
    import com.beam.assetManagement.assetRecon.IpData.SubdomainPortData;
    import com.beam.assetManagement.assetRecon.SubdomainData.SubdomainDataService;
    import com.beam.assetManagement.assetRecon.SubdomainDataDetails.SubdomainDataDetailService;
    import com.beam.assetManagement.assets.Asset;
    import com.beam.assetManagement.assets.AssetRepository;
    import com.beam.assetManagement.user.UserService;
    import com.beam.assetManagement.utils.RegexMatcherService;
    import lombok.RequiredArgsConstructor;
    import lombok.extern.slf4j.Slf4j;
    import org.apache.commons.net.whois.WhoisClient;
    import org.springframework.stereotype.Component;

    import java.io.BufferedReader;
    import java.io.IOException;
    import java.io.InputStreamReader;
    import java.util.*;
    import java.util.regex.Matcher;
    import java.util.regex.Pattern;


    @Component
    @RequiredArgsConstructor
    @Slf4j


    public class AssetEnumerationService {

        private final AssetRepository assetRepository;

        private final RegexMatcherService regexMatcherService;

        public static String assetDomainName;

        private final SubdomainDataService subdomainDataService;

        private final SubdomainDataDetailService subdomainDataDetailService;

        private final IpDataService ipDataService;

        private final UserService userService;







        public List<String> setAsset(String assetDomainName,String firmId,String userId, String scanSpeed) throws Exception {

            String role = userService.isSuperUser(userId);
            Optional<Asset> asset = null;

            if(role=="SUPER_USER"){
                asset = assetRepository.findByAssetDomain(assetDomainName);
            }
            else {
                asset = assetRepository.findByAssetDomainAndFirmId(assetDomainName,firmId);
            }


            if (asset.isPresent()) {
                Asset testAsset = asset.get();
                String assetId = testAsset.getId();
                String modifiedDomain = assetDomainName.substring(4);
                Set<String> uniqueSubdomainIds = new HashSet<>();
                ipDataService.detectPort(assetId);
                 subdomainDataDetailService.getSubdomains(modifiedDomain,uniqueSubdomainIds);

                subdomainDataService.SaveSubdomainData(uniqueSubdomainIds,assetId,firmId);

                ipDataService.getIpFromDataDetailsObject(subdomainDataDetailService.getDataDetailsObjectById(assetId),assetId);

                ipDataService.insertPortScanToObject(assetId,firmId,scanSpeed);

                assetRepository.save(testAsset);
                return null;
            }
            else{

                throw new IllegalAccessException("Asset with the provided asset domain name not found.");
            }


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







