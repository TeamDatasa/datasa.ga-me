package datasa.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/chat")
public class ChatRoomController {

    @GetMapping("/room/{tripId}")
    public String chatRoom(
            @PathVariable Long tripId,
            Model model
    ) {

        model.addAttribute("tripId", tripId);
        return "chat-room";
    }
}
