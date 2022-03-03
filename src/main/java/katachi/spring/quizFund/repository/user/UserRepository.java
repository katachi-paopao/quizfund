package katachi.spring.quizFund.repository.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import katachi.spring.quizFund.model.user.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
	Optional<User> findByMail(String mail);
	Optional<User> findByUuid(String uuid);
}
