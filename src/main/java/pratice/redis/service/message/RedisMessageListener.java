package pratice.redis.service.message;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class RedisMessageListener {

    //Spring의 STOMP 메시징 기능을 간단하게 사용할 수 있는 클래스
    // -. 서버에서 클라이언트로 STOMP 메시지를 전송하는 데 사용되며,
    //  주로 @MessaggeMapping으로 처리되지 않는 비동기메시지 전송에 사용한다.
    // -. 메시지 브로커에 의해 메시지를 전달한다.
    private final SimpMessagingTemplate messagingTemplate;

    //메시지를 처리할 클래스의 메소드
    // -. MessageListenerAdapter 에 등록해서 사용한다.
    public void receiveMessage(String message) {
        log.info(">>> Received message : {}", message);
        messagingTemplate.convertAndSend("/topic/alarm", message);
    }
}
