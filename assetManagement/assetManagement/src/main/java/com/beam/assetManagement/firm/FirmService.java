package com.beam.assetManagement.firm;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FirmService {
    private final FirmRepository firmRepository;

    public void CreateFirm(FirmRequest firmRequest){


        if(firmRepository.existsByFirmName(firmRequest.getFirmName())){
            throw new IllegalArgumentException("All fields in the RegistrationRequest must be provided.");
        }else {
            Firm firm = Firm.builder()
                    .id(UUID.randomUUID().toString())
                    .firmName(firmRequest.getFirmName())
                    .firmLocation(firmRequest.getFirmLocation())
                    .build();
            firmRepository.save(firm);
        }


    }


    public List<Firm> getAllFirms(){
         return firmRepository.findAll();
    }
}
