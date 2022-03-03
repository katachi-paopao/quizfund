package katachi.spring.quizFund.model;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name="quizset_item")
public class IntermediateQuizAndQuizset {
	@EmbeddedId
	private IntermediateQuizAndQuizsetPK key;

	@Column
	private int serial;
}
