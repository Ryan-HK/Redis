package pratice.redis.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import pratice.redis.service.message.RedisPublisher;

@Controller
@RequiredArgsConstructor
public class MessageController {


    private final RedisPublisher redisPublisher;




    @GetMapping("/api/message")
    @ResponseBody
    public String triggerEvent(@RequestParam String message) {

        if (message == null) message = "hello world";

        redisPublisher.publishEvent("event-channel", message);

        return "OK";
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }
}
