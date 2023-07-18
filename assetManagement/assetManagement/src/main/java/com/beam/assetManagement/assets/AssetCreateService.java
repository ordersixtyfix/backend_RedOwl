package com.beam.assetManagement.assets;

import com.beam.assetManagement.security.validator.AssetValidator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class AssetCreateService {


    private final AssetValidator assetValidator;

    private final AssetService assetService;



    public String createAsset(AssetRequest request) {

        boolean isValidAsset = assetValidator.test(request.getAssetName());

        if(!isValidAsset){
            throw new IllegalStateException("email not valid");
        }

        return assetService.registerAsset(

                new Asset(


                        request.getAssetName(),
                        request.getAssetIpAddress(),
                        request.getAssetLocation(),
                        request.getAssetDomain()


                )

        );
    }





}
