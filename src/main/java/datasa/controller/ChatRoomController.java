package datasa.controller;

import datasa.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatService chatService;

    @GetMapping("/room/{tripId}")
    public String chatRoom(@PathVariable Long tripId, Model model) {
        Long roomId = chatService.getOrCreateRoomIdByTrip(tripId);

        model.addAttribute("tripId", tripId);
        model.addAttribute("roomId", roomId);
        return "chat-room";
    }
}

