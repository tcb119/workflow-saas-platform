package com.cb.workflow.auth.dto;

import lombok.Data;

@Data
public class TokenResponse {

    // Authorization header 的 token type（授權憑證類型），英文意思比較像是：持有者
    private String tokenType = "Bearer";
    private String accessToken;
    private String refreshToken;


    // 用一個 static 方法來建立物件，而不是直接用 new。
    // 為什麼叫「工廠（factory）」？因為它在做的是：負責「產生物件」的地方（而且可以加邏輯）
    // 這邊的 語意是： 「幫我產一個 token response」，而不是：「new 一個東西（但不知道裡面在幹嘛）」
    // 與可以控制「初始化邏輯」，例如：r.setTokenType("Bearer"); // 固定
    public static TokenResponse of(String access, String refresh) {
        TokenResponse r = new TokenResponse();
        r.setAccessToken(access);
        r.setRefreshToken(refresh);
        return r;
    }
}