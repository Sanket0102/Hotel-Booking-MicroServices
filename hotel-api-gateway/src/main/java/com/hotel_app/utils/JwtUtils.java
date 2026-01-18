package com.hotel_app.utils;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.List;

@Component
public class JwtUtils {

    private String secret = "36763979244226452948404D635166546A576D5A7134743777217A25432A462D";

    private Key key(){
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    public boolean validateToken(String jwtToken){
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key())
                    .build()
                    .parseClaimsJws(jwtToken);
            return true;
        }
        catch (MalformedJwtException e) {
            System.out.println("invalid Jwt token : " + e.getMessage());
        }
        catch(ExpiredJwtException e){
            System.out.println("Expired Jwt Token : " + e.getMessage());
        }
        catch(UnsupportedJwtException e){
            System.out.println("Unsupported Jwt token : " + e.getMessage());
        }
        catch(IllegalArgumentException e){
            System.out.println("No claims found : " + e.getMessage());
        }
        return false;
    }

    public String getUsernameFromToken(String token){
        return Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public List getRolesFromToken(String token){
        return Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("roles",List.class);
    }


}