package com.beam.assetManagement.user;

import com.beam.assetManagement.security.validator.EmailValidator;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final static String USER_NOT_FOUND_MSG = "user with email %s not found";
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final EmailValidator emailValidator;



    @PostConstruct
    private void postConstruct(){



        User admin = new User("Gul","Bora","admin@admin.com","+5%sko7d!", AppUserRole.SUPER_USER);


        boolean userExists = userRepository.findByEmail(admin.getEmail())
                .isPresent();

        if(!userExists){

            String encodedPassword = bCryptPasswordEncoder.encode(admin.getPassword());

            admin.setPassword(encodedPassword);

            userRepository.save(admin);
        }






    }


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {


        Optional<User> account = userRepository.findByEmail(email);
        if (account.isEmpty()) throw new UsernameNotFoundException ("User Not Found");
        return new org.springframework.security.core.userdetails.User(account.get().getEmail(), account.get().getPassword(), AuthorityUtils.createAuthorityList(account.get().getAppUserRole().toString()));




        //return userRepository.findByEmail(email)
        //       .orElseThrow(()->new UsernameNotFoundException(String.format(USER_NOT_FOUND_MSG, email)));
    }

    public User signUpUser(User user){
        boolean userExists = userRepository.findByEmail(user.getEmail())
                .isPresent();

        if(userExists){
            throw new IllegalStateException("Email already taken");
        }


        String encodedPassword = bCryptPasswordEncoder.encode(user.getPassword());

        user.setPassword(encodedPassword);

        userRepository.save(user);


        //TODO: Send confirmation token



        return user;
    }

}
