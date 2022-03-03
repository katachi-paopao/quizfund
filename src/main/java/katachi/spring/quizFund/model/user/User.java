package katachi.spring.quizFund.model.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name="user")
public class User {
	@Id
	@Column
	private String uuid;

	@Column
	private String mail;

	@Column
	private String password;

	@Column
	private String name;
}
