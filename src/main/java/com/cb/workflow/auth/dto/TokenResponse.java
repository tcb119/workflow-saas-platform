package com.cb.workflow.auth.dto;

import lombok.Data;

@Data
public class TokenResponse {
    private String tokenType = "Bearer";
    private String accessToken;
    private String refreshToken;

    public static TokenResponse of(String access, String refresh) {
        TokenResponse r = new TokenResponse();
        r.setAccessToken(access);
        r.setRefreshToken(refresh);
        return r;
    }
}