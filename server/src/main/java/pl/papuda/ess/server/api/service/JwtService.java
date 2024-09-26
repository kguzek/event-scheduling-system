package pl.papuda.ess.server.api.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import pl.papuda.ess.server.api.model.User;

@Service
public class JwtService {

    @Value("${JWT_SECRET}")
    private String SECRET_KEY;

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private Date extractExpiryDate(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiryDate(token).before(new Date());
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    public boolean isEmailTokenValid(String token, String email) {
        final String encodedEmail = extractUsername(token);
        return (encodedEmail.equals(email) && !isTokenExpired(token));
    }

    public String generateToken(User user) {
        return generateToken(new HashMap<>(), user);
    }

    public String generateToken(Map<String, Object> extraClaims, User user) {
        final long currentTime = System.currentTimeMillis();
        final long expiryTime = currentTime + 1000 * 60 * 60 * 12; // Expires in 12 hours
        return Jwts.builder().claims(extraClaims).subject(user.getUsername()).issuedAt(new Date(currentTime))
                .expiration(new Date(expiryTime)).signWith(getSigningKey(), Jwts.SIG.HS256).compact();
    }

    public String generateEmailToken(String email) {
        final long currentTime = System.currentTimeMillis();
        final long expiryTime = currentTime + 1000 * 60 * 30; // Expires in 30 minutes
        return Jwts.builder().subject(email).issuedAt(new Date(currentTime))
                .expiration(new Date(expiryTime)).signWith(getSigningKey(), Jwts.SIG.HS256).compact();
    }
}
