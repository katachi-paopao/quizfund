package katachi.spring.quizFund.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedSubgraph;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@NoArgsConstructor
@Table(name="quizset")
@NamedEntityGraph(name="Quizset.game",
	attributeNodes = {
			@NamedAttributeNode(value="name"),
			@NamedAttributeNode(value="quizList", subgraph="quizset.quizList")
	},
	subgraphs = {
			@NamedSubgraph(name="quizset.quizList",
					attributeNodes = {
							@NamedAttributeNode("id"),
							@NamedAttributeNode("format"),
							@NamedAttributeNode("text"),
							@NamedAttributeNode("appearance"),
							@NamedAttributeNode("correct"),
							@NamedAttributeNode("user"),
							@NamedAttributeNode(value="option", subgraph="quizset.quiz.option"),
							@NamedAttributeNode(value="answers", subgraph="quizset.quiz.answers")
					}
			),
			@NamedSubgraph(name="quizset.quizList.option",
					attributeNodes = {
							@NamedAttributeNode("ans_length"),
							@NamedAttributeNode("ans_type")
					}
			),
			@NamedSubgraph(name="quizset.quiz.answers",
					attributeNodes = {
							@NamedAttributeNode("answer"),
					}
			)
	}
)

@NamedEntityGraph(name="Quizset.summary",
attributeNodes = {
		@NamedAttributeNode(value="name"),
		@NamedAttributeNode(value="introduction"),
		@NamedAttributeNode(value="isOpen"),
		@NamedAttributeNode(value="playCount"),
		@NamedAttributeNode(value="quizList", subgraph="quizset.quizList")
},
subgraphs = {
		@NamedSubgraph(name="quizset.quizList",
				attributeNodes = {
						@NamedAttributeNode("id"),
				}
		)
}
)

public class Quizset {
	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column
	private String name;

	@Column(name="created_by")
	private String user;

	@Column(name="introduction")
	private String introduction;

	@Column(name="is_open")
	private boolean isOpen;

	@Column(name="play_count")
	private int playCount;

	@Column(name="is_random")
	private boolean isRandom;

	@Column(name="num")
	private int num;

	@ManyToMany(fetch=FetchType.LAZY)
	@JoinTable(name="quizset_item",
			joinColumns=@JoinColumn(name="quizset_id"),
	        inverseJoinColumns=@JoinColumn(name="quiz_id")
	)
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@JsonIgnore
	private List<Quiz> quizList;

	public void removeQuiz(Quiz quiz)  {
		quizList.remove(quiz);
	}
}
