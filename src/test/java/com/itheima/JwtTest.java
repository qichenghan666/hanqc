package com.itheima;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtTest {
    @Test
    public void testGen(){
        Map<String,Object> claims = new HashMap<>();
        claims.put("id",1);
        claims.put("username","张三");
        String token = JWT.create()
                .withClaim("user",claims)
                .withExpiresAt( new Date(System.currentTimeMillis() + 60*60*1000*12))
                .sign(Algorithm.HMAC256("itheima"));
        System.out.println(token);
    }
    @Test
    public void testParse(){
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9" +
                ".eyJ1c2VyIjp7ImlkIjoxLCJ1c2VybmFtZSI6IuW8oOS4iSJ9LCJleHAiOjE3MjcwNTMxNDN9" +
                ".qONHGodcbcpeKoXfi0mbQsoQlUse_9m31unejP6lNY8";
        DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC256("itheima")).build().verify(token);
        Map<String, Claim> claims = decodedJWT.getClaims();
        System.out.println(claims);
    }
}
