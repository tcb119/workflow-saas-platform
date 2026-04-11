package com.cb.workflow.security.jwt;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class RefreshTokenSupport {

    //•	Random：為了速度與一般用途設計，可預測性較高
	//•	SecureRandom：為了安全用途設計，不可預測性高（cryptographically strong）
    private final SecureRandom random = new SecureRandom();

    public String newRawToken() {

        // 這是一個48 bytes 的陣列，每個 byte = 8 bits → 總共 48×8 = 384 bits 的隨機性
        // 這裡準備了一個能容納 48 個隨機位元組的陣列，用來生成高熵（high entropy）的 token。
        byte[] bytes = new byte[48];
        random.nextBytes(bytes);
        //Base64 是什麼？ 把「二進位資料（bytes）」轉成「可傳輸的字串」。
        //因為 bytes 不能直接安全地在 HTTP header / JSON 中傳（[亂碼 bytes] → "AbCdEf123..."）
        //而一般 Base64 會包含：「+ / =」這些字元在 URL / HTTP 中容易出問題，所以 URL-safe 版本會改成：
        // + 變成 - ， /變成 _ ，= 可以保留，也可以去掉，只是這邊去掉 padding（=`）
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public String sha256Base64Url(String raw) {

    // 為什麼 try-catch？因為理論上：
    //	•	某些環境可能沒有這個演算法（雖然幾乎不可能）
    // 所以：
    // 👉 如果真的沒有 → 系統應該直接 fail
    try {

            /*
            * 為了降低資料庫洩漏風險，refresh token 不會直接以明文儲存，
            * 而是先透過 SHA-256 轉成不可逆的 hash 後再存入資料庫。這樣即使資料庫被洩漏，攻擊者也無法還原原始 token。
            * 同時系統可以透過比對 hash 來驗證 token 是否有效，並在 logout 時透過 revoke 機制將其標記為失效。*/
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            //raw token (字串)→ 轉 bytes
            // → SHA-256 計算
            // → 得到 digest（bytes）（hash 計算後的「結果」（但還是 bytes 形式）
            // 再 Base64 變字串）
            byte[] digest = md.digest(raw.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
        } catch (Exception e) {
            throw new IllegalStateException("SHA-256 unavailable", e);
        }
    }
}