package com.beam.assetManagement.utils;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.beam.assetManagement.assetRecon.AssetEnumeration.trimBeforeColon;

@Service
public class RegexMatcherService {




    public List<String> checkRegistrarData(String whoisResponse){

        String patternServer = "Registrar WHOIS Server:\\s*(.*)";
        String patternUrl = "Registrar URL:\\s*(.*)";
        String patternId = "Registry Domain ID:\\s*(.*)";
        String patternRegName = "Registrar:\\s*(.*)";
        String patternIANA_Id = "Registrar IANA ID:\\s*(.*)";
        String patternRegContact = "Registrar Abuse Contact Email:\\s*(.*)";
        String patternRegExpireDate = "Registry Expiry Date:\\s*(.*)";
        String patternRegCreateDate = "Creation Date:\\s*(.*)";

        Pattern regex = Pattern.compile(patternServer, Pattern.CASE_INSENSITIVE);
        Pattern regex2 = Pattern.compile(patternUrl, Pattern.CASE_INSENSITIVE);
        Pattern regex3 = Pattern.compile(patternId, Pattern.CASE_INSENSITIVE);
        Pattern regex4 = Pattern.compile(patternRegName, Pattern.CASE_INSENSITIVE);
        Pattern regex5 = Pattern.compile(patternIANA_Id, Pattern.CASE_INSENSITIVE);
        Pattern regex6 = Pattern.compile(patternRegContact, Pattern.CASE_INSENSITIVE);
        Pattern regex7 = Pattern.compile(patternRegExpireDate, Pattern.CASE_INSENSITIVE);
        Pattern regex8 = Pattern.compile(patternRegCreateDate, Pattern.CASE_INSENSITIVE);

        Matcher matcher =   regex.matcher(whoisResponse);
        Matcher matcher2 = regex2.matcher(whoisResponse);
        Matcher matcher3 = regex3.matcher(whoisResponse);
        Matcher matcher4 = regex4.matcher(whoisResponse);
        Matcher matcher5 = regex5.matcher(whoisResponse);
        Matcher matcher6 = regex6.matcher(whoisResponse);
        Matcher matcher7 = regex7.matcher(whoisResponse);
        Matcher matcher8 = regex8.matcher(whoisResponse);

        List<String> registrarData = new ArrayList<>();

        while(matcher.find() && matcher2.find() && matcher3.find() && matcher4.find() && matcher5.find() &&
                matcher6.find() && matcher7.find() && matcher8.find()){
            String _regServer = matcher.group();
            String _regUrl = matcher2.group();
            String _regDomainId = matcher3.group();
            String _regName  = matcher4.group();
            String _regIANA_Id  = matcher5.group();
            String _regContactEmail = matcher6.group();
            String _regExpireDate  = matcher7.group();
            String _regCreateDate  = matcher8.group();


            String regData1 = trimBeforeColon(_regServer);
            String regData2 = trimBeforeColon(_regUrl);
            String regData3 = trimBeforeColon(_regDomainId);
            String regData4 = trimBeforeColon(_regName);
            String regData5 = trimBeforeColon(_regIANA_Id);
            String regData6 = trimBeforeColon(_regContactEmail);
            String regData7 = trimBeforeColon(_regExpireDate);
            String regData8 = trimBeforeColon(_regCreateDate);

            registrarData.addAll(Arrays.asList(regData1,regData2, regData3,regData4,regData5,regData6,regData7,regData8));

            System.out.println(registrarData);
        }
        return registrarData;

    }






}
