package study.springjwt.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import study.springjwt.jwt.JWTFilter;
import study.springjwt.jwt.JWTUtil;
import study.springjwt.jwt.LoginFilter;

import java.util.Collection;
import java.util.Collections;

@Configuration //스프링이 configuration으로 관리
@EnableWebSecurity //시큐리티를 위한 config
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final JWTUtil jwtUtil;

    @Bean //로그인 필터의 인자로 auth매니저를 넣어주기 위해
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {

        return configuration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() { //비밀번호를 해시 암호화 시킬 수 있다.

        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        //cors처리
        http
                .cors(cors -> cors
                        .configurationSource(new CorsConfigurationSource() {
                            @Override
                            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {

                                CorsConfiguration configuration = new CorsConfiguration();

                                //cors에 대해 각 값들을 설정해준다.
                                configuration.setAllowedOrigins(Collections.singletonList("http://localhost:3000")); //3000번 포트를 허용
                                configuration.setAllowedMethods(Collections.singletonList("*")); //모든 메소드를 허용
                                configuration.setAllowCredentials(true);
                                configuration.setAllowedHeaders(Collections.singletonList("*")); //모든 헤더 허용
                                configuration.setMaxAge(3600L); //허용하는 시간

                                configuration.setExposedHeaders(Collections.singletonList("Authorization")); //클라이언트로 헤더를 보낼 때, Authorization에 jwt를 넣어서 보낼거라서 Authorization 헤더도 허용

                                return configuration;
                            }
                        }));

        //csrf를 disable 설정 - 세션방식과 달리 JWT방식은 csrf에 대한 공격을 방어하지 않아도 된다.
        http
                .csrf(auth -> auth.disable());

        //Form 로그인 방식 disable
        http
                .formLogin(auth -> auth.disable());

        //http basic 인증 방식 disable
        http
                .httpBasic(auth -> auth.disable());

        //경로별 인가 작업 - 컨트롤러의 어떤 경로에서 어떤 권한을 가지는지, 로그인을 해야하는지를 설정
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/", "/join").permitAll() //해당 경로에선 모든 권한 허용
                        .requestMatchers("/admin").hasRole("ADMIN") //해당 경로에선 admin만 권한 허용
                        .anyRequest().authenticated()); //이외의 요청에서는 권한 허용x

        //jwt토큰 검증 필터 등록
        http
                .addFilterBefore(new JWTFilter(jwtUtil), LoginFilter.class); //로그인 필터 앞에 jwt필터 추가

        //커스텀 필터 등록
        http
                .addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil), UsernamePasswordAuthenticationFilter.class); //UsernamePasswordAuthenticationFilter를 대체하므로 그 자리에 등록

        //세션 설정 - JWT방식은 세션을 stateless상태로 설정해야한다! 가장 중요!
        http
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));


        return http.build();
    }
}
