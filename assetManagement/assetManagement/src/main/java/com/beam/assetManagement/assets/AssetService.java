package com.beam.assetManagement.assets;

import com.beam.assetManagement.assetRecon.IpData.IpDataRepository;
import com.beam.assetManagement.user.User;
import com.beam.assetManagement.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AssetService {


    private final AssetRepository assetRepository;
    private final UserRepository userRepository;
    private final IpDataRepository ipDataRepository;


    public boolean createAsset(AssetRequest request) {
        validateAssetRequest(request);

        return registerAsset(Asset.builder().assetName(request.getAssetName()).assetIpAddress(request.getAssetIpAddress()).assetLocation(request.getAssetLocation()).assetDomain(request.getAssetDomain()).firmId(request.getFirmId()).build()

        );
    }

    public boolean createAssetBySuperUser(AssetRequest request) {
        validateAssetRequest(request);

        return registerAsset(Asset.builder().assetName(request.getAssetName()).assetIpAddress(request.getAssetIpAddress()).assetLocation(request.getAssetLocation()).assetDomain(request.getAssetDomain()).firmId(request.getFirmId()).build()

        );
    }

    private void validateAssetRequest(AssetRequest request) {

        if (request == null || request.getAssetName() == null || request.getAssetName().isEmpty() || request.getAssetIpAddress() == null || request.getAssetIpAddress().isEmpty() || request.getAssetDomain() == null || request.getAssetDomain().isEmpty() || request.getAssetLocation() == null || request.getAssetLocation().isEmpty()) {
            throw new IllegalArgumentException("All fields in the RegistrationRequest must be provided.");
        }
    }

    private boolean registerAsset(Asset asset) {
        try {
            if (assetRepository.findByAssetNameOrAssetDomainOrAssetIpAddress(asset.getAssetName(), asset.getAssetDomain(), asset.getAssetIpAddress()).isPresent()) {
                throw new IllegalStateException("Asset already exists.");
            }

            asset.setId(UUID.randomUUID().toString());
            assetRepository.save(asset);


            return true;
        } catch (Exception e) {

            throw new IllegalStateException("Asset registration failed:");
        }
    }


    public long getIpCount(String firmId) {

        Optional<User> user = userRepository.findFirstByFirmId(firmId);
        String role = String.valueOf(user.get().getAppUserRole());

        if(role=="SUPER_USER"){
            return ipDataRepository.count();
        }else {
            return ipDataRepository.findByFirmId(firmId).stream().count();
        }

    }

    public long getAssetCount(String firmId,String userId) {

        Optional<User> user = userRepository.findById(userId);
        String role = String.valueOf(user.get().getAppUserRole());


        if (role == "SUPER_USER") {
            return assetRepository.findAll().stream().count();

        } else {
            List<Asset> assetList = assetRepository.findByFirmId(firmId);
            return assetList.stream().count();
        }


    }

    public Optional<Asset> getAssetByAssetName(boolean isAdmin, String assetName, String firmId){
        if(isAdmin){
            return assetRepository.findByAssetName(assetName);
        }else {
            return assetRepository.findByAssetNameAndFirmId(assetName,firmId);
        }

    }

    public Optional<Asset> getAssetById(String assetId) {
        return assetRepository.findById(assetId);
    }

    public Page<Asset> getAssetByFirmId(String firmId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return assetRepository.findByFirmId(firmId, pageRequest);
    }

    public Page<Asset> getAllAssets(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);

        return assetRepository.findAll(pageRequest);
    }

    public boolean userValidation(String userId) {


        Optional<User> user = userRepository.findById(userId);
        String role = String.valueOf(user.get().getAppUserRole());

        return role.equals("SUPER_USER") ? true : false;


    }


}