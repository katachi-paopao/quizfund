package katachi.spring.quizFund.model.game;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Question {
	private String text;
	private Integer ans_length;
	private Integer ans_type;
	private int car; // 正解率（correct answer rate）
}
