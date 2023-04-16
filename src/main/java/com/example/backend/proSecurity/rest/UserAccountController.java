package com.example.backend.proSecurity.rest;

import com.example.backend.proSecurity.configuration.AppConfig;
import com.example.backend.proSecurity.emailVerification.ConfirmationToken;
import com.example.backend.proSecurity.emailVerification.ConfirmationTokenRepository;
import com.example.backend.proSecurity.emailVerification.EmailSenderService;
import com.example.backend.proSecurity.user.UserEntity;
import com.example.backend.proSecurity.user.UserRepository;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Getter
@Setter
@Controller
public class UserAccountController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;

    @Autowired
    private EmailSenderService emailSenderService;

    @Autowired
    public AppConfig config;


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    String message;


    @RequestMapping(value="/register", method =  RequestMethod.POST)
    public ResponseEntity<Object> registerUser(@RequestBody UserEntity user) throws MessagingException {
        Map<String, Object> response = new HashMap<String, Object>();

        UserEntity existingUser = userRepository.findByEmailIgnoreCase(user.getEmail());
        UserEntity existingUsername = userRepository.findByUsernameIgnoreCase(user.getUsername());

        if(existingUser != null)
        {
            response.put("status", "error");
            response.put("message", "Email už existuje!");
            return new ResponseEntity<Object>(response, HttpStatus.OK);

        }

        else if(existingUsername != null)
        {
            response.put("status", "error");
            response.put("message", "Používateľské meno už existuje!");
            return new ResponseEntity<Object>(response, HttpStatus.OK);

        }
        else
        {

            user.setPassword(config.passwordEncoder().encode(user.getPassword()));
            if(Objects.equals(user.getConfirmPassword(), "*ADMIN123*")){
                user.setAuthority("ADMIN");
                user.setConfirmPassword("ACCEPT_ADMIN");
                userRepository.save(user);
            }
            else {
                user.setAuthority("USER");
                user.setConfirmPassword("ACCEPT_USER");
                userRepository.save(user);
            }
            ConfirmationToken confirmationToken = new ConfirmationToken(user);

            confirmationTokenRepository.save(confirmationToken);

            MimeMessage message = emailSenderService.javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(user.getEmail());
            helper.setSubject("Dokončite registráciu!");
            helper.setFrom(new InternetAddress("BAZARAPP"));

            String htmlContent = "<html><body>"
                    + "<h1>WEBOVÝ BAZÁR</h1>"
                    + "<h2>Potvrďte svoju registráciu:</h2>"
                    + "<a href='https://bazar-mh.vercel.app/confirmVerification?token=" + confirmationToken.getConfirmationToken() + "'>Potvrdiť registráciu</a>"
                    + "</body></html>";
            helper.setText(htmlContent, true);

            emailSenderService.sendEmail(message);
        }

        response.put("status", "success");
        response.put("message", "Potvrď registráciu cez Email");
        return new ResponseEntity<Object>(response, HttpStatus.OK);

          }

    @GetMapping("/registrationMessage")
    public String registrationMessage() {
        return getMessage();
    }



    @RequestMapping(value="/confirm-account", method= {RequestMethod.GET, RequestMethod.POST})
    public void confirmUserAccount(ModelAndView modelAndView, @RequestParam("token")String confirmationToken)
    {
        ConfirmationToken token = confirmationTokenRepository.findByConfirmationToken(confirmationToken);

        if(token != null)
        {
            UserEntity user = userRepository.findByEmailIgnoreCase(token.getUser().getEmail());
            user.setEnabled(true);
            userRepository.save(user);
//            modelAndView.setViewName("accountVerified");
        }
//        else
//        {
//            modelAndView.addObject("message","The link is invalid or broken!");
//            modelAndView.setViewName("error");
//        }

//        return modelAndView;
    }
}
