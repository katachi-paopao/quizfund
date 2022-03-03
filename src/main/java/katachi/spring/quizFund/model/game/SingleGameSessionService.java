package katachi.spring.quizFund.model.game;

import java.util.Date;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import katachi.spring.quizFund.model.websocket.GameMessage;
import katachi.spring.quizFund.service.GameService;

@Service
public class SingleGameSessionService {
	@Autowired
	GameService gs;

	@Autowired
	SimpMessagingTemplate smt;

	private static HashMap<String, Game> map = new HashMap<String, Game>();
	private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	public boolean start(String connectionId, int qsid) {
		Optional<Game> game = gs.initGame(qsid);
		if (game.isPresent()) {
			map.put(connectionId, game.get());
			return true;
		}
		return false;
	}

	public void terminate(String connectionId) {
		Game g = map.get(connectionId);
		if (g != null) {
			ScheduledFuture<?> handler = g.getTimeoutHandler();
			if (handler != null) {
				g.getTimeoutHandler().cancel(false);
				g.setTimeoutHandler(null);
			}
			map.remove(connectionId);
		}
	}

	public GameMessage next(String connectionId) {
		Game g = map.get(connectionId);
		if (g != null) {
			Optional<Question> question = g.next();
			if (question.isPresent()) {
				GameMessage msg = new GameMessage("question", question);
				g.setLastSendTime(new Date().getTime());
				notifyTimeout(g, connectionId);
				return msg;
			} else {
				GameMessage msg = new GameMessage("end", g.getResultList());
				g.updatePlayCount(g.getQsid());
				g.updateCorrectRate();
				return msg;
			}
		}
		GameMessage msg = new GameMessage("error", "ゲームが中断されました");
		return msg;
	}

	public GameMessage check(String connectionId, String userAnswer) {
		Game g = map.get(connectionId);
		try {
			double leftTime = (new Date().getTime() - g.getLastSendTime()) / 1000;
			g.getTimeoutHandler().cancel(false);
			g.setTimeoutHandler(null);
			GameMessage msg = new GameMessage("check", g.check(userAnswer, leftTime) ? true : false);
			return msg;
		} catch (IndexOutOfBoundsException e) {
			GameMessage msg = new GameMessage("error", "解答を取得できません");
			return msg;
		}
	}

	public void notifyTimeout(Game game, String connectionId) {
		game.setTimeoutHandler(scheduler.schedule(new Runnable() {
			@Override
			public void run() {
				GameMessage msg = new GameMessage("timeout");
				smt.convertAndSend("/topic/game/" + connectionId, msg);
				try {
					game.check("", 0);
					Thread.sleep(3000);
					msg = next(connectionId);
					smt.convertAndSend("/topic/game/" + connectionId, msg);
				} catch (InterruptedException e) {
					e.printStackTrace();
					msg = new GameMessage("error", "サーバー側エラー");
					smt.convertAndSend("/topic/game/" + connectionId, msg);
				}
			}
		}, 21, TimeUnit.SECONDS));
	}
}