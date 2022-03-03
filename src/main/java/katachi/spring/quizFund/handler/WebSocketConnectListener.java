package katachi.spring.quizFund.handler;

import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;

@Component
public class WebSocketConnectListener implements ApplicationListener<SessionConnectEvent> {

	@Override
	public void onApplicationEvent(SessionConnectEvent e) {
		StompHeaderAccessor sha = StompHeaderAccessor.wrap(e.getMessage());
		System.out.println(sha);
	}
}
