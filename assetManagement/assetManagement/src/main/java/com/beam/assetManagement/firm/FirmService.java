package com.beam.assetManagement.firm;

import com.beam.assetManagement.user.User;
import com.beam.assetManagement.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FirmService {
    private final FirmRepository firmRepository;

    private final UserRepository userRepository;


    public void createFirm(FirmRequest firmRequest){


        if(firmRepository.existsByFirmName(firmRequest.getFirmName()) || firmRequest.getFirmLocation().isEmpty()
                || firmRequest.getFirmName().isEmpty()){
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

    public void deleteFirm(String firmId,String userId){
        Optional<User> user = userRepository.findById(userId);
        try{
            if (user.isPresent()) {
                String role = String.valueOf(user.get().getAppUserRole());

                if ("SUPER_USER".equals(role)) {
                    firmRepository.deleteById(firmId);
                }
            }
        }catch (Exception e){
           
        }

    }


    public List<Firm> getAllFirms(){
         return firmRepository.findAll();
    }

    public String getFirmName(String firmId){
        Optional<Firm> firm = firmRepository.findById(firmId);

        return firm.get().getFirmName();
    }


}
