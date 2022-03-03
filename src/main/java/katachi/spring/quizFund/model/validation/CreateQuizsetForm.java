package katachi.spring.quizFund.model.validation;

import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;

import lombok.Data;

@Data
public class CreateQuizsetForm {
	@NotBlank
	@Length(max=100)
	private String name;

	@Length(max=200)
	private String intro;
}
