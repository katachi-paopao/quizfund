package katachi.spring.quizFund.repository.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import katachi.spring.quizFund.model.user.Authorization;
import katachi.spring.quizFund.model.user.AuthorizationPK;

public interface AuthorizationRepository extends JpaRepository<Authorization, AuthorizationPK> {
	Optional<Authorization> findByKey(AuthorizationPK key);
}
