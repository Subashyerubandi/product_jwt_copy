package com.subash.product_jwt_copy.service;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.subash.product_jwt_copy.dto.ForgetPasswordRequest;
import com.subash.product_jwt_copy.dto.PasswordChangeRequest;
import com.subash.product_jwt_copy.dto.RegisterRequest;
import com.subash.product_jwt_copy.dto.UserDTO;
import com.subash.product_jwt_copy.exception.EmailAlreadyExistsException;
import com.subash.product_jwt_copy.exception.PhoneAlreadyExistsException;
import com.subash.product_jwt_copy.model.Role;
import com.subash.product_jwt_copy.model.User;
import com.subash.product_jwt_copy.repository.RoleRepository;
import com.subash.product_jwt_copy.repository.UserRepository;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    public User registerUser(RegisterRequest request, boolean isAdmin) {
        // Validate email uniqueness
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }

        // Validate phone uniqueness
        if (userRepository.findByMobile(request.getMobile()).isPresent()) {
            throw new IllegalArgumentException("Phone number already exists");
        }

        // Validate password matching
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        // Map DTO to entity
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(encoder.encode(request.getPassword()));
        user.setMobile(request.getMobile());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setGender(request.getGender());

        // Assign roles
        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new IllegalStateException("Role USER not found"));
        roles.add(userRole);

        if (isAdmin) {
            Role adminRole = roleRepository.findByName("ADMIN")
                    .orElseThrow(() -> new IllegalStateException("Role ADMIN not found"));
            roles.add(adminRole);
        }

        user.setRoles(roles);

        // Save and return user
        return userRepository.save(user);
    }
    
    @Transactional(readOnly = true)
    public UserDTO getUser() {
    	String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return convertToDTO(user);
    }

    @Transactional
    public UserDTO updateUser(UserDTO userDTO) {
    	
    	String email = SecurityContextHolder.getContext().getAuthentication().getName();
    	
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Validate unique constraints
        if (!user.getEmail().equals(userDTO.getEmail()) && 
            userRepository.existsByEmail(userDTO.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists: " + userDTO.getEmail());
        }
        if (!user.getMobile().equals(userDTO.getMobile()) && 
            userRepository.existsByMobile(userDTO.getMobile())) {
            throw new PhoneAlreadyExistsException("Phone already exists: " + userDTO.getMobile());
        }

        // Update fields
        user.setEmail(userDTO.getEmail());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setMobile(userDTO.getMobile());
        user.setGender(userDTO.getGender());

        User updatedUser = userRepository.save(user);
        return convertToDTO(updatedUser);
    }
    
    public void changePassword(PasswordChangeRequest request) {
        // Get the authenticated user's email from SecurityContext
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        // Find the user by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // Verify the old password
        if (encoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }

        // Validate new password and confirmation
        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            throw new IllegalArgumentException("New password and confirmation do not match");
        }

        // Encode and update the new password
        user.setPassword(encoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
    
    public void changePasswordThroughMobile(String mobile, ForgetPasswordRequest request) {

    	User user = userRepository.findByMobile(mobile)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with mobile: " + mobile));

    	// Verify the old password
        if (encoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Entered same password as old password");
        }
    	
        // Validate new password and confirmation
        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            throw new IllegalArgumentException("New password and confirmation do not match");
        }

        // Encode and update the new password
        user.setPassword(encoder.encode(request.getNewPassword()));
        userRepository.save(user);
		
	}

	public void changePasswordThroughEmail(String email, ForgetPasswordRequest request) {
		
		User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

		// Verify the old password
        if (encoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Entered same password as old password");
        }
		
        // Validate new password and confirmation
        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            throw new IllegalArgumentException("New password and confirmation do not match");
        }

        // Encode and update the new password
        user.setPassword(encoder.encode(request.getNewPassword()));
        userRepository.save(user);
        
	}

    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setMobile(user.getMobile());
        dto.setGender(user.getGender());
        dto.setRoles(user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet()));
        return dto;
    }
    
}