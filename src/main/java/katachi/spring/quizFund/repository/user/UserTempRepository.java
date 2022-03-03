package katachi.spring.quizFund.repository.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import katachi.spring.quizFund.model.user.UserTemp;

@Repository
public interface UserTempRepository extends JpaRepository<UserTemp, String> {
	UserTemp findByUuid(String uuid);
	Optional<UserTemp> findByMail(String mail);
	void deleteByUuid(String uuid);
}
