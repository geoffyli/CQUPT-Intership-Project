package com.sensonet.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.util.*;

/**
 * This class is used to generate and verify JWT token.
 */
@Slf4j
public class JwtUtil {
    public static final String JWT_KEY = "123456";

    public static final long JWT_EXPIRED_KEY = 30 * 60 * 1000L; // Half an hour

    public static List<String> blacklist = new ArrayList<>();

    /**
     * Generate the secret key
     *
     * @return SecretKey
     */
    public static String createJWT(Integer adminId) {

        // Set the signature algorithm to use HS256
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        // Get the current time and add the timeout time
        long nowMillis = System.currentTimeMillis();
        long expTime = nowMillis + JWT_EXPIRED_KEY;
        // Build the JWT
        JwtBuilder builder = null;
        try {
            builder = Jwts.builder()
                    .setId(UUID.randomUUID() + "")
                    .setSubject(JsonUtil.serialize(adminId))
                    .setIssuer("system")
                    .setIssuedAt(new Date(nowMillis))
                    .signWith(signatureAlgorithm, encodeSecret())
                    .setExpiration(new Date(expTime));
        } catch (JsonProcessingException e) {
            log.error("json serialize failed", e);
        }
        assert builder != null;
        return builder.compact();

    }

    private static SecretKey encodeSecret() {
        byte[] encode = Base64.getEncoder().encode(JwtUtil.JWT_KEY.getBytes());
        return new SecretKeySpec(encode, 0, encode.length, "AES");
    }


    /**
     * Parse the JWT token
     * @param token JWT token
     * @return Claims
     */
    public static Claims parseJWT(String token){
        return Jwts.parser()
                .setSigningKey(encodeSecret())
                .parseClaimsJws(token)
                .getBody();
    }

    public static <T> T parseJWT(String token, Class<T> clazz){
        Claims body = Jwts.parser()
                .setSigningKey(encodeSecret())
                .parseClaimsJws(token)
                .getBody();
        try {
            return JsonUtil.getByJson(body.getSubject(),clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void invalidateJWT(String token){
        blacklist.add(token);
    }

    public static boolean inBlacklist(String token){
        return blacklist.contains(token);
    }


}
