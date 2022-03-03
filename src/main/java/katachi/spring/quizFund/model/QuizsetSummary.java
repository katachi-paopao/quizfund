package katachi.spring.quizFund.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizsetSummary {
	private int id;
	private String name;
	private String introduction;
	private boolean isOpen;
	private boolean isRandom;
	private int quizNum;
	private int gameNum;
	private int playCount;
	private String editor;
}
