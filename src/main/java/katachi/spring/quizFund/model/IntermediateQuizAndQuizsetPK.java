package katachi.spring.quizFund.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IntermediateQuizAndQuizsetPK implements Serializable{
	@Column(name="quizset_id")
	private int quizsetId;

	@Column(name="quiz_id")
	private int quizId;
}
