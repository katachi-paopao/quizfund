package katachi.spring.quizFund.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpAttributesContextHolder;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import katachi.spring.quizFund.model.game.SingleGameSessionService;
import katachi.spring.quizFund.model.websocket.GameMessage;

@Controller
public class WebsocketController {
	@Autowired
	SingleGameSessionService sgss;

	@Autowired
	SimpMessagingTemplate smt;

	@MessageMapping("/websocket/game/{id}")
	public void startGameSession(@DestinationVariable String id, int qsid) throws Exception {
		Thread.sleep(1000);

		/* コネクションIDをコンテクストに登録 */
		SimpAttributesContextHolder.currentAttributes().setAttribute("connectionId", id);

		/*  ゲーム開始メッセージを通知 */
		GameMessage msg = new GameMessage("start");
		smt.convertAndSend("/topic/game/" + id, msg);

		/* ゲームセッションの開始 */
		if (sgss.start(id, qsid)) {
			smt.convertAndSend("/topic/game/" + id, sgss.next(id));
		} else {
			msg = new GameMessage("error", "クイズセットの取得に失敗しました");
			smt.convertAndSend("/topic/game/" + id, msg);
		}
	}

	@MessageMapping("/websocket/game/answer")
	public void checkAnswer(GameMessage getMsg) throws Exception {
			String id = (String) SimpAttributesContextHolder.currentAttributes().getAttribute("connectionId");
			GameMessage resMsg = sgss.check(id, getMsg.getObject().toString());
			smt.convertAndSend("/topic/game/" + id, resMsg);

			Thread.sleep(3000);

			smt.convertAndSend("/topic/game/" + id, sgss.next(id));
	}

	/* メッセージオブジェクトの引数マッピング失敗処理 */
	@MessageExceptionHandler
	(org.springframework.messaging.converter.MessageConversionException.class)
	public void messageConversionExceptionHandler() {
		String id = (String) SimpAttributesContextHolder.currentAttributes().getAttribute("connectionId");
		GameMessage msg = new GameMessage("error", "不正なリクエストです");
		smt.convertAndSend("/topic/game/" + id, msg);
	}
}
