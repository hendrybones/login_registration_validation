package com.example.validation.registration;

import com.example.validation.email.EmailSender;
import com.example.validation.systemuser.User;
import com.example.validation.systemuser.UserRole;
import com.example.validation.systemuser.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RegistrationService {
    private final UserService userService;
    private final EmailValidator emailValidator;
    private final EmailSender emailSender;
    public String register(RegistrationRequest request) {
        boolean isValidEmail=emailValidator.test(request.getEmail());
        if (!isValidEmail){
            throw new IllegalStateException("email not valid");
        }
        String token = userService.signUpUser(new User(
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                request.getPassword(),
                UserRole.User


        ));
        emailSender.send(request.getEmail(),);
        return token;
    }

}
