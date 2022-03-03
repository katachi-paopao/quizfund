package katachi.spring.quizFund.model.game;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class Result {
	private Question question;
	private List<String> answers;
	private boolean correct;
	private int score;

	public Result(Question question) {
		this.question = question;
		this.answers = new ArrayList<String>();
	}
}
