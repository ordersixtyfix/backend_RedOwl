package com.beam.assetManagement.assets;

import com.beam.assetManagement.assetRecon.IpData.IpDataRepository;
import com.beam.assetManagement.security.validator.AssetValidator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class AssetService {


    private final AssetRepository assetRepository;


    private final IpDataRepository ipDataRepository;


    private AssetValidator assetValidator;



    public String registerAsset(Asset asset) {


        boolean assetExists = assetRepository.findByAssetName(asset.getAssetName()).isPresent();

        if (assetExists) {
            throw new IllegalStateException("Asset already exists");
        }


        System.out.println("CHECK");

        String id = UUID.randomUUID().toString();

        asset.setAssetId(id);

        assetRepository.save(asset);


        //TODO: Send confirmation token


        return "it works";
    }

    public String createAsset(AssetRequest request) {

        boolean isValidAsset = assetValidator.test(request.getAssetName());

        if (!isValidAsset) {
            throw new IllegalStateException("email not valid");
        }

        return this.registerAsset(

                new Asset(


                        request.getAssetName(),
                        request.getAssetIpAddress(),
                        request.getAssetLocation(),
                        request.getAssetDomain()


                )

        );
    }

    public long getIpCount(){
        return ipDataRepository.count();
    }

    public long getAssetCount(){
        return assetRepository.count();
    }


}
