package com.beam.assetManagement.user;


import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final static String USER_NOT_FOUND_MSG = "user with email %s not found";
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;




    @PostConstruct
    private void postConstruct() {


        User admin = User.builder().lastName("Gul").firstName("Bora").email("admin@admin.com").password("+5%sko7d!")
                .appUserRole(AppUserRole.SUPER_USER).firmId(UUID.randomUUID().toString()).build();


        boolean userExists = userRepository.findByEmail(admin.getEmail()).isPresent();

        if (!userExists) {

            String encodedPassword = bCryptPasswordEncoder.encode(admin.getPassword());

            admin.setPassword(encodedPassword);
            admin.setId(UUID.randomUUID().toString());

            userRepository.save(admin);
        }


    }


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {


        Optional<User> account = userRepository.findByEmail(email);
        if (account.isEmpty()) throw new UsernameNotFoundException("User Not Found");
        return new org.springframework.security.core.userdetails.User(account.get().getEmail(), account.get().getPassword(), AuthorityUtils.createAuthorityList(account.get().getAppUserRole().toString()));


    }

    public User signUpUser(User user) {
        boolean userExists = userRepository.findByEmail(user.getEmail()).isPresent();

        if (userExists) {
            throw new IllegalStateException("Email already taken");
        }


        String encodedPassword = bCryptPasswordEncoder.encode(user.getPassword());

        user.setPassword(encodedPassword);
        user.setId(UUID.randomUUID().toString());

        userRepository.save(user);
        return user;
    }


    public UserDto getUserByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);


        return UserDto.builder()
                .id(user.get().getId())
                .firstName(user.get().getFirstName())
                .lastName(user.get().getLastName())
                .email(user.get().getEmail())
                .appUserRole(user.get().getAppUserRole())
                .firmId(user.get().getFirmId()).build();

    }


    public String isSuperUser(String userId) {

        String role = String.valueOf(userRepository.findById(userId).get().getAppUserRole());
        return role;

    }

    public UserDto getFirstUserByFirmId(String firmId){
        Optional<User> user = userRepository.findFirstByFirmId(firmId);
        return UserDto.builder()
                .id(user.get().getId())
                .firstName(user.get().getFirstName())
                .lastName(user.get().getLastName())
                .email(user.get().getEmail())
                .appUserRole(user.get().getAppUserRole())
                .firmId(user.get().getFirmId()).build();
    }


    public String getFirmIdByUserId(String userId){
        Optional<User> user = userRepository.findById(userId);
        String firmId = user.get().getFirmId();
        return firmId;
    }





}



