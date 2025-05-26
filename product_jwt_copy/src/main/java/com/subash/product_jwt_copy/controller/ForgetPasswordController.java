package com.subash.product_jwt_copy.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.subash.product_jwt_copy.dto.ForgetPasswordRequest;
import com.subash.product_jwt_copy.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/forgetPassword")
public class ForgetPasswordController {
	
	private final UserService userService;
	
	@PostMapping("/mobile")
    public ResponseEntity<String> changePasswordThroughMobile(@RequestParam String mobile, @RequestBody ForgetPasswordRequest request) {
        try {
            userService.changePasswordThroughMobile(mobile, request);
            return ResponseEntity.ok("Password changed successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
	
	@PostMapping("/email")
    public ResponseEntity<String> changePasswordThroughEmail(@RequestParam String email, @RequestBody ForgetPasswordRequest request) {
        try {
            userService.changePasswordThroughEmail(email, request);
            return ResponseEntity.ok("Password changed successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
	
}
