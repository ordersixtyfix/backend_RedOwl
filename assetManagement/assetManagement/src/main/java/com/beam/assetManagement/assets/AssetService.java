package com.beam.assetManagement.assets;

import com.beam.assetManagement.security.validator.AssetValidator;
import com.beam.assetManagement.security.validator.EmailValidator;
import com.beam.assetManagement.user.User;
import com.beam.assetManagement.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class AssetService {





    private final AssetRepository assetRepository;


    public String registerAsset(Asset asset){



        boolean assetExists = assetRepository.findByAssetName(asset.getAssetName())
                .isPresent();

        if(assetExists){
            throw new IllegalStateException("Asset already exists");
        }


        System.out.println("CHECK");

        String id = UUID.randomUUID().toString();

        asset.setAssetId(id);

        assetRepository.save(asset);


        //TODO: Send confirmation token



        return "it works";
    }
}
