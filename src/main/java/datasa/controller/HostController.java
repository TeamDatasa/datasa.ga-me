package datasa.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/host")
public class HostController {

    @GetMapping("/applications")
    public String hostApplicationPage() {
        return "host-application"; // templates/host-application.html
    }
}
