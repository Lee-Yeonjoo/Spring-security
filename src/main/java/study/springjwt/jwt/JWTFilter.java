package study.springjwt.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import study.springjwt.dto.CustomUserDetails;
import study.springjwt.entity.UserEntity;

import java.io.IOException;

@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter { //요청에 대해 한번만 동작하는 OncePerRequestFilter를 상속받는다. 이 필터를 SecurityConfig에 등록

    private final JWTUtil jwtUtil; //jwt 검증을 위해 필요

    //필터의 내부 구현
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        //토큰 검증 구현
        //request에서 Authorization키 값을 가진 헤더를 추출
        String authorization = request.getHeader("Authorization");

        //토큰이 null이거나 접두사 Bearer가 아닌 경우(토큰이 제대로 되지x), 이 메소드를 종료
        if (authorization == null || !authorization.startsWith("Bearer ")) {

            System.out.println("token null");
            filterChain.doFilter(request, response); //체인방식으로 엮여있는 필터들을 종료하고, request와 response를 다음 필터로 넘겨준다.

            return; //메소드 종료(필수)
        }

        //토큰에서 접두사 부분을 제거
        String token = authorization.split(" ")[1]; //공백을 기준으로 자른 리스트의 인덱스1에 토큰이 존재(0에는 "Bearer")

        //토큰의 소멸시간이 지나지 않았는지 검증
        if (jwtUtil.isExpired(token)) { //토큰이 만료되었다면 메소드 종료

            System.out.println("token expired");
            filterChain.doFilter(request, response);

            return;
        }

        //토큰을 기반으로 일시적인 세션을 만들어서 시큐리티 세션에 일시적으로 저장. -> 권한을 요구하는 경로에 접근 가능해진다.
        //토큰에서 username과 role 추출
        String username = jwtUtil.getUsername(token);
        String role = jwtUtil.getRole(token);

        //추출한 username과 role로 유저엔티티를 세팅 - 이때 password는 정확할 필요가 없어서 temppassword를 넣는다.
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(username);
        userEntity.setPassword("temppassword");
        userEntity.setRole(role);

        //UserDetails에 userEntity객체를 넣기
        CustomUserDetails customUserDetails = new CustomUserDetails(userEntity);

        //스프링 시큐리티 auth토큰 객체 생성
        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());

        //세션에 사용자 등록 - 다른 요청 경로에 접근 가능해진다.
        SecurityContextHolder.getContext().setAuthentication(authToken);

        //이제 메소드가 종료되므로 필터 체인을 통해 다음 필터에게 request와 response를 넘긴다.
        filterChain.doFilter(request, response);
    }
}
