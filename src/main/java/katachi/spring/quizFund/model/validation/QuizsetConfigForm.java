package katachi.spring.quizFund.model.validation;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;

import lombok.Data;

@Data
public class QuizsetConfigForm {
	@NotBlank
	@Length(max=100)
	private String name;

	@Length(max=200)
	private String intro;

	private boolean random;

	@Min(1)
	@Max(50)
	private int gameNum;
}