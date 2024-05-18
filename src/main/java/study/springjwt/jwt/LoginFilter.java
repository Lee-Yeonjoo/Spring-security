package study.springjwt.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import study.springjwt.dto.CustomUserDetails;

import java.util.Collection;
import java.util.Iterator;

@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter { //회원 검증을 위한 커스텀 필터 -> SecurityConfig에 등록해야한다.

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        //클라이언트 요청에서 username과 password를 꺼낸다.
        String username = obtainUsername(request);
        String password = obtainPassword(request);
        System.out.println("username = " + username); //요청에서 잘 추출했는지 확인

        //꺼낸 값으로 인증 진행하기 위해 필터가 AuthenticationManager에게 username과 password를 넘겨야 되는데 이때 dto로 넘겨야 한다.
        //dto는 UsernamePasswordAuthenticationToken
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password, null);

        //AuthenticationManager에게 전달 -> 이제 매니저가 검증 진행
        return authenticationManager.authenticate(authToken);
    }

    //검증 성공 시 실행되는 메소드
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) {

        //jwt 발급
        //user객체를 알아내기 위함
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal(); //특정 유저를 가져온다.

        //customUserDetails에서 username 추출
        String username = customUserDetails.getUsername();

        //authentication에서 role값 추출
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        //추출한 username과 role을 JWTUtil에 전달해 토큰 발급
        String token = jwtUtil.createJwt(username, role, 60 * 60 * 10L);

        //발급받은 jwt를 response의 헤더에 담아서 응답
        response.addHeader("Authorization", "Bearer " + token); //Authorization은 키값. jwt토큰 앞에 Bearer이라는 접두사를 붙이고 띄어써야 한다.
    }

    //검증 실패 시 실행되는 메소드
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {

        response.setStatus(401);
    }
}
