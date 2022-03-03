package katachi.spring.quizFund.model.user;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(name="authorization")
public class Authorization {
	@EmbeddedId
	private AuthorizationPK key;
	private String user_uuid;

}
