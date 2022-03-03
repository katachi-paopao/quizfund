package katachi.spring.quizFund.model.user;

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
public class AuthorizationPK implements Serializable {
		@Column
		private String provider_name;

		@Column
		private String provider_id;
}
