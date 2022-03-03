package katachi.spring.quizFund.model.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(name="user_temp")
public class UserTemp {
	@Id
	@Column
	private String uuid;

	@Column
	private String mail;

	@Column
	private String password;

	@Column
	private String name;

	@Column
	private String created_on;


}
