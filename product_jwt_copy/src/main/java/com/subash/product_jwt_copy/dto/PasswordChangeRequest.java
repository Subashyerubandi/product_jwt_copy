package com.subash.product_jwt_copy.dto;

import lombok.Data;

@Data
public class PasswordChangeRequest {
	private String oldPassword;
    private String newPassword;
    private String confirmNewPassword;
}
