package katachi.spring.quizFund.handler;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import katachi.spring.quizFund.model.game.SingleGameSessionService;

@Component
public class WebSocketDisconnectListener implements ApplicationListener<SessionDisconnectEvent> {
	@Autowired
	SingleGameSessionService sgss;

	@Override
	public void onApplicationEvent(SessionDisconnectEvent e) {
		StompHeaderAccessor sha = StompHeaderAccessor.wrap(e.getMessage());
		Map<String, Object>attr = sha.getSessionAttributes();
		sgss.terminate((String)attr.get("connectionId"));
	}
}
