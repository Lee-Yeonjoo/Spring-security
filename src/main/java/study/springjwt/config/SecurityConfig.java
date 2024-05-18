package study.springjwt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration //스프링이 configuration으로 관리
@EnableWebSecurity //시큐리티를 위한 config
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() { //비밀번호를 해시 암호화 시킬 수 있다.

        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

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

        //세션 설정 - JWT방식은 세션을 stateless상태로 설정해야한다! 가장 중요!
        http
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));


        return http.build();
    }
}
