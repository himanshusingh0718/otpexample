package com.otpexample.service;

import com.otpexample.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.HashMap;
import java.util.Map;

@Service
public class EmailVerificationService {
    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    static final Map<String,String> emailOtpMapping = new HashMap<>();
    public Map<String,String> verifyOtp(String email,String otp) {
        String storedOtp = emailOtpMapping.get(email);

        Map<String, String> response = new HashMap<>();
        if (storedOtp != null && storedOtp.equals(otp)){
        // fetch user by email and mark email as verified
        //logger.info("OTP is valid. Proceeding with verification.");
        User user = userService.getUserByEmail(email);
        if (user != null) {
            emailOtpMapping.remove(email);
            userService.verifyEmail(user);
            response.put("Status", "Success");
            response.put("Message", "Email verified successfully");
        } else {
           // logger.error("Invalid OTP received for email: {}", email);
            response.put("Status", "error");
            response.put("Message", "user not found");
        }
        } else {
        response.put("Status","error");
        response.put("Message","Invalid OTP");
        }

        return response;

    }
    
   
    
    public Map<String,String> sendOtpForLogin(String email){
        if(userService.isEmailVerified(email)) {
            String otp = emailService.generateOtp();
            emailOtpMapping.put(email, otp);

            //send OTP to user's email
            emailService.sendOtpEmail(email);

            Map<String, String> response = new HashMap<>();
            response.put("Status", "Success");
            response.put("Message", "OTP sent successfully");
            return response;
        } else {
            Map<String,String> resposne = new HashMap<>();
            resposne.put("Status","Error");
            resposne.put("Message","Email is not verified");
            return resposne;
        }
        
    }

    public Map<String, String> verifyOtpForLogin(String email, String otp) {
        String storedOtp = emailOtpMapping.get(email);

        Map<String,String> response = new HashMap<>();
        if (storedOtp != null && storedOtp.equals(otp)) {
            emailOtpMapping.remove(email);
            //OTP is valid
            response.put("Status", "Success");
            response.put("Message", "OTP is verified successfully");
        } else {
            //Invalid OTP
            response.put("Status","Error");
            response.put("Message","Invalid OTP");
        }
        return response;
    }
}

