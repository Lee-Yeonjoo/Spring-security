package study.springjwt.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsMvcConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry corsRegistry) {

        corsRegistry.addMapping("/**") //모든 컨트롤러 경로에 대해 프론트 쪽에서 요청이 오는 주소(localhost:3000)를 설정
                .allowedOrigins("http://localhost:3000");
    }
}
