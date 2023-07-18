package com.beam.assetManagement.security.validator;

import org.springframework.stereotype.Service;

import java.util.function.Predicate;
@Service
public class AssetValidator implements Predicate<String> {

    @Override
    public boolean test(String s){
        return true;
    }





}
