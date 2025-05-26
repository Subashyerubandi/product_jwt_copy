package com.subash.product_jwt_copy.dto;

import java.util.Set;

import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String mobile;
    private String gender;
    private Set<String> roles;
}
