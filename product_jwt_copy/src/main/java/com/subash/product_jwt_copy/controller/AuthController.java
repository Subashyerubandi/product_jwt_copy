package com.subash.product_jwt_copy.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.subash.product_jwt_copy.dto.LoginRequest;
import com.subash.product_jwt_copy.dto.RegisterRequest;
import com.subash.product_jwt_copy.dto.UserDTO;
import com.subash.product_jwt_copy.exception.ErrorResponse;
import com.subash.product_jwt_copy.model.Role;
import com.subash.product_jwt_copy.model.User;
import com.subash.product_jwt_copy.repository.UserRepository;
import com.subash.product_jwt_copy.security.JwtTokenProvider;
import com.subash.product_jwt_copy.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = {"http://localhost:5173", "http://192.168.1.101:5173"})
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtTokenProvider.generateToken(authentication);

            User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
            UserDTO userDTO = mapToUserDTO(user);

            // Return JWT and user details in response body
            Map<String, Object> response = new HashMap<>();
            response.put("token", jwt);
            response.put("user", userDTO);

            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse("Login failed", "Invalid email or password", "/api/auth/login"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // No server-side session or cookie to clear
        return ResponseEntity.ok("Logout successful");
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        User user = userService.registerUser(registerRequest, false);
        Map<String, String> response = new HashMap<>();
        response.put("message", "User registered successfully");
        response.put("email", user.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/register/admin")
    public ResponseEntity<?> registerAdmin(@Valid @RequestBody RegisterRequest registerRequest) {
        User user = userService.registerUser(registerRequest, true);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Admin registered successfully");
        response.put("email", user.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    private UserDTO mapToUserDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setEmail(user.getEmail());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        userDTO.setMobile(user.getMobile());
        userDTO.setGender(user.getGender());
        userDTO.setRoles(user.getRoles().stream()
            .map(Role::getName)
            .collect(Collectors.toSet()));
        return userDTO;
    }
}