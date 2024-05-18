package study.springjwt.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter { //회원 검증을 위한 커스텀 필터 -> SecurityConfig에 등록해야한다.

    private final AuthenticationManager authenticationManager;

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

        System.out.println("success");
    }

    //검증 실패 시 실행되는 메소드
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {

        System.out.println("fail");
    }
}
