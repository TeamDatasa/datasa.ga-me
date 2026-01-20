package datasa.controller;


import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class MainController {

    @GetMapping("/")
    public String main(HttpSession session, Model model) {

        Long loginMemberId =
                (Long) session.getAttribute("loginMemberId");

        boolean isLogin = (loginMemberId != null);


        model.addAttribute("isLogin", isLogin);

        return "trip-test";
    }

}

