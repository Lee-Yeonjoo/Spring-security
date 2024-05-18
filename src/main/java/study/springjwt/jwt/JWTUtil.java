package study.springjwt.jwt;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JWTUtil {

    private SecretKey secretKey;

    public JWTUtil(@Value("${spring.jwt.secret}") String secret) { //yml파일의 변수값을 가져온다.

        //가져온 secret 문자열을 SecretKey 객체타입으로 저장 - jwt에서는 문자열이 아닌 객체타입의 키를 쓴다.
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    //검증을 진행하는 메소드 - username, role, 토큰이 만료됐는지 검증
    public String getUsername(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("username", String.class);
    }

    public String getRole(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("role", String.class);
    }

    public boolean isExpired(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    }

    //토큰을 생성하는 메소드 - 토큰은 String
    public String createJwt(String username, String role, Long expiredMs) {

        return Jwts.builder()
                .claim("username", username)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis())) //토큰 발행 시간(현재 시간)
                .expiration(new Date(System.currentTimeMillis() + expiredMs)) //토큰 만료 시간 = 토큰 발행 시간 + 유효기간
                .signWith(secretKey) //시크릿키를 통해(시그니처를 통해) 토큰 암호화
                .compact(); //토큰을 컴팩트시킴
    }
}
