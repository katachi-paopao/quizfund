package katachi.spring.quizFund.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@NoArgsConstructor
@Table(name="quiz")
@NamedEntityGraph(name="Quiz.all",
		attributeNodes = {
				@NamedAttributeNode("id"),
				@NamedAttributeNode("format"),
				@NamedAttributeNode("text"),
				@NamedAttributeNode("appearance"),
				@NamedAttributeNode("correct"),
				@NamedAttributeNode("user"),
				@NamedAttributeNode(value="option", subgraph="quiz.option"),
				@NamedAttributeNode(value="answers", subgraph="quiz.answers")
		},
		subgraphs = {
				@NamedSubgraph(name="quiz.option",
						attributeNodes = {
								@NamedAttributeNode("ans_length"),
								@NamedAttributeNode("ans_type")
						}
				),
				@NamedSubgraph(name="quiz.answers",
						attributeNodes = {
								@NamedAttributeNode("answer"),
						}
				)
		}
)
public class Quiz implements Serializable {
	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(nullable = false)
	private int format;

	@Column(nullable = false)
	private String text;

	@Column(nullable = false)
	private int appearance;

	@Column(nullable = false)
	private int correct;

	@Column(nullable = false, name="created_by")
	private String user;

	@Transient
	private int serial;

	/*
		cascade=CascadeType.Allを指定するとエラーになりますが、原因がよくわかりません
		（hibernateのdeleteの削除の順序が
			answer -> quiz -> optionなのが一因のような気がしますが、
			この順序の変更の仕方もよくわかりません）
		データベースで ON DELETE CASCADE が指定されている場合は、
		こちら側で指定が漏れていてもCASCADE削除が行われるので、指定を外しています。
	 */
	@OneToOne(optional=false, fetch=FetchType.LAZY)
	@JoinColumn(name="id", updatable=false)
	private TypingOption option;

	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name="id", updatable=false)
	private List<TypingAnswer> answers;

	@ManyToMany
	@JoinTable(name="quizset_item",
		joinColumns=@JoinColumn(name="quiz_id"),
		inverseJoinColumns=@JoinColumn(name="quizset_id")
	)
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@JsonIgnore
	private List<Quizset> quizset;

	public Quiz(int format, String text, String uuid) {
		this.format = format;
		this.text = text;
		this.appearance = 0;
		this.correct = 0;
		this.user = uuid;
	}
}
