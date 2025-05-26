package com.subash.product_jwt_copy.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class AddressDTO {
    private Long id;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Mobile number must be 10 digits")
    private String mobileNumber;

    @NotBlank(message = "Pincode is required")
    @Pattern(regexp = "^[0-9]{6}$", message = "Pincode must be 6 digits")
    private String pincode;

    @NotBlank(message = "Locality is required")
    private String locality;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "City is required")
    private String city;
    
    @NotBlank(message = "State is required")
    private String state;

    @Pattern(regexp = "^$|^[0-9]{10}$", message = "Alternate phone must be 10 digits")
    private String alternatePhone;

    private String landmark;

    @NotBlank(message = "Address type is required")
    private String addressType;
}