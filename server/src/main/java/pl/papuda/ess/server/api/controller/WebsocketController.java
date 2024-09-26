package pl.papuda.ess.server.api.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import pl.papuda.ess.server.api.model.body.EmailChallengeRequest;

@Controller
@RequestMapping("/api/v1")
public class WebsocketController {

    @MessageMapping("/auth/email/challenge")
    @SendTo("/auth/email/verified")
    public String greeting(EmailChallengeRequest message) {
        return message.getEmail();
    }
}
