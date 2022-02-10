package com.example.validation.systemuser;

import com.example.validation.token.ConfirmationToken;
import com.example.validation.token.ConfirmationTokenService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {

    private final static String USER_NOT_FOUND_MSG=
            "user with email %s not found";
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private  final ConfirmationTokenService confirmationTokenService;

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(()->
                        new UsernameNotFoundException(String.format(
                                USER_NOT_FOUND_MSG,email)));
    }
    public String signUpUser(User user){
        boolean userExist=userRepository
                .findByEmail(user.getEmail())
                .isPresent();
        if (userExist){
            throw new IllegalStateException("email already taken");
        }
        String encodePassword =bCryptPasswordEncoder
                .encode(user.getPassword());
        user.setPassword(encodePassword);
        userRepository.save(user);
        String token=UUID.randomUUID().toString();
        //TODO: send confirmation password
        ConfirmationToken confirmationToken=new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                user
        );
        confirmationTokenService.saveConfirmationToken(confirmationToken);
        //TODO: send email
        return token;
    }
}
