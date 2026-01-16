package datasa.controller;

import datasa.entity.Course;
import datasa.repository.CourseRepository;
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

        List<Course> recommendCourses =
                CourseRepository.findTop5ByOrderByViewCountDesc();

        model.addAttribute("isLogin", isLogin);
        model.addAttribute("recommendCourses", recommendCourses);

        return "main";
    }
}

