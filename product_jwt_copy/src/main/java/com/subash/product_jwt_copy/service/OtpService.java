package com.subash.product_jwt_copy.service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class OtpService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private final long EXPIRE_MINUTES = 1;

    public String generateOtpForMobile(String phoneNumber) {
        String otp = String.valueOf(new Random().nextInt(900000) + 100000); // 6-digit OTP
        redisTemplate.opsForValue().set(phoneNumber, otp, EXPIRE_MINUTES, TimeUnit.MINUTES);

        // Simulate SMS sending by printing to console
        System.out.println("Sending OTP " + otp + " to mobile number: " + phoneNumber);

        return otp;
    }

    public boolean validateOtpForMobile(String phoneNumber, String otpInput) {
        String otpStored = redisTemplate.opsForValue().get(phoneNumber);
        return otpStored != null && otpStored.equals(otpInput);
    }

    public String generateOtpForEmail(String email) {
    	String otp = String.valueOf(new Random().nextInt(900000) + 100000); // 6-digit OTP
        redisTemplate.opsForValue().set(email, otp, EXPIRE_MINUTES, TimeUnit.MINUTES);

        // Simulate SMS sending by printing to console
        System.out.println("Sending OTP " + otp + " to email: " + email);

        return otp;
    }

    public boolean validateOtpForEmail(String email, String otpInput) {
    	String otpStored = redisTemplate.opsForValue().get(email);
        return otpStored != null && otpStored.equals(otpInput);
    }
}
