package katachi.spring.quizFund.model.websocket;

import java.io.Serializable;

import lombok.Data;

@Data
public class GameMessage implements Serializable {
	private String action;
	private Object object;

	public GameMessage(String action) {
		this.action = action;
	}

	public GameMessage(String action, Object object) {
		this.action = action;
		this.object = object;
	}
}
