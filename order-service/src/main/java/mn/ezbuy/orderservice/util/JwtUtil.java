package mn.ezbuy.orderservice.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.security.Key;
import java.util.Date;
import java.util.Objects;


@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    private Key key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public ResponseEntity<?> verifyTokenAndAdmin(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        if(claims.getExpiration().before(new Date())) {
            return new ResponseEntity<>("Token has expired!", HttpStatus.UNAUTHORIZED);
        } else {
            if(claims.get("role",String.class).equals("ADMIN")) {
                return new ResponseEntity<>(claims.toString(),HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Action is not allowed!",HttpStatus.FORBIDDEN);
            }
        }
    }

    public ResponseEntity<?> verifyToken(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        if(claims.getExpiration().before(new Date())) {
            return new ResponseEntity<>("Token has expired!", HttpStatus.UNAUTHORIZED);
        } else {
            return new ResponseEntity<>(claims.toString(),HttpStatus.OK);
        }
    }

    public ResponseEntity<?> verifyTokenAndAuthorization(String token, Long id) {
        Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        if(claims.getExpiration().before(new Date())) {
            return new ResponseEntity<>("Token has expired!", HttpStatus.UNAUTHORIZED);
        } else {
            if(Objects.equals(claims.get("id", Long.class), id) || claims.get("role",String.class).equals("ADMIN")) {
                return new ResponseEntity<>(claims.toString(),HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Token is invalid!",HttpStatus.FORBIDDEN);
            }
        }
    }

}
