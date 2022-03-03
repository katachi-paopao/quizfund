package katachi.spring.quizFund.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TypingAnswerPK implements Serializable {
	@Column(name="id")
	private int id;

	@Column(name="serial")
	private int serial;
}
