package katachi.spring.quizFund.model.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;

import katachi.spring.quizFund.model.Quiz;
import katachi.spring.quizFund.model.TypingAnswer;
import katachi.spring.quizFund.service.QuizService;
import lombok.Data;

@Data
public class Game {
	private int qsid;
	private List<Quiz> list;
	private List<Result> resultList;
	private int progress;
	private long lastSendTime;
	private ScheduledFuture<?> timeoutHandler;
	private QuizService qs;

	public Game(int qsid, List<Quiz> list, QuizService qs) {
		this.qsid = qsid;
		this.list = list;
		this.resultList = new ArrayList<Result>();
		this.progress = 0;
		this.qs = qs;
	}

	public Optional<Question> next() {
		Question res = null;
		if (progress < list.size()) {
			Quiz quiz = list.get(progress);
			progress++;
			res = new Question(quiz.getText(), quiz.getOption().getAns_length(),
					quiz.getOption().getAns_type(), 0);
			resultList.add(new Result(res));
		};

		return Optional.ofNullable(res);
	}

	public boolean check(String userAnswer, double leftTime) {
		List<TypingAnswer> answers = getList().get(progress-1).getAnswers();
		Result result = resultList.get(progress-1);
		List<String> resultAnswers = result.getAnswers();
		for (TypingAnswer answer : answers) {
			resultAnswers.add(answer.getAnswer());
			if (userAnswer.equals(answer.getAnswer()) && leftTime < 20 && leftTime > 0) {
				result.setCorrect(true);
				result.setScore((int)calcScore(leftTime));
			}
		}
		return result.isCorrect();
	}

	public double calcScore(double leftTime) {
		return leftTime <= 5 ? 100 : 100 * (1 - Math.pow(0.01, Math.pow(Math.E, -0.25 * (leftTime - 5))));
	}

	public boolean updatePlayCount(int qsid) {
		try {
			qs.updatePlayCount(qsid);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean updateCorrectRate() {
		for (int i=0, len=list.size(); i<len; i++) {
			qs.updateCorrectRate(list.get(i).getId(), resultList.get(i).isCorrect());
		}
		return false;
	}
}
