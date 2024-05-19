package study.springjwt.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collection;
import java.util.Iterator;

@Controller
@ResponseBody //웹 서버(웹페이지를 리턴)로 동작하지 않고, API 서버로 동작
public class MainController {

    @GetMapping("/")
    public String mainP() {

        //세션의 사용자의 아이디를 가져온다.
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        //세션의 사용자의 role값을 가져온다.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iter = authorities.iterator();
        GrantedAuthority auth = iter.next();
        String role = auth.getAuthority();

        return "Main Controller " + username + " " + role;
    }
}
