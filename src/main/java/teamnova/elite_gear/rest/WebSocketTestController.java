package teamnova.elite_gear.rest;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import teamnova.elite_gear.model.MessageResponse;

import java.util.Date;

@Controller
public class WebSocketTestController {

    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketTestController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/test-message")
    @SendTo("/topic/test-response")
    public MessageResponse handleTestMessage(String message) {
        // Return a proper JSON object instead of a plain string
        return new MessageResponse("Server received: " + message);
    }

    @GetMapping("/api/test-websocket")
    @ResponseBody
    public String testWebSocket() {
        // Send a proper JSON object
        MessageResponse testMessage = new MessageResponse(
                "Test message from server at: " + new Date()
        );
        System.out.println("Sending test message: " + testMessage);
        messagingTemplate.convertAndSend("/topic/test-response", testMessage);
        return "Test message sent";
    }
}