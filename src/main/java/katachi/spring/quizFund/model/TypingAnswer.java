package katachi.spring.quizFund.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(name="typing_answer")
public class TypingAnswer implements Serializable {
	@EmbeddedId
	private TypingAnswerPK key;

	@Column
	private String answer;

	public TypingAnswer (int id, int serial, String answer) {
		this.key =  new TypingAnswerPK(id, serial);
		this.answer = answer;
	}
}
