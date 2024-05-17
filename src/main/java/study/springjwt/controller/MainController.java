package study.springjwt.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody //웹 서버(웹페이지를 리턴)로 동작하지 않고, API 서버로 동작
public class MainController {

    @GetMapping("/")
    public String mainP() {

        return "Main Controller";
    }
}
