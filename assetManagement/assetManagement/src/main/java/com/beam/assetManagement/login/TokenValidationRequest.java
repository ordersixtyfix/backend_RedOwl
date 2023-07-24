package com.beam.assetManagement.login;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor

public class TokenValidationRequest {

    private String token;
}
