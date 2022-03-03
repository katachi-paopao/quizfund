package katachi.spring.quizFund.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(name="typing_option")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // 消すとQuizset APIがエラーで機能しません、原因がよくわかりません。
public class TypingOption implements Serializable {
	@Id
	@Column
	private int id;

	@Column
	private Integer ans_length;

	@Column
	private Integer ans_type;

	public TypingOption (int id, Integer ans_length, Integer ans_type) {
		this.id = id;
		this.ans_length = ans_length;
		this.ans_type = ans_type;
	}
}
