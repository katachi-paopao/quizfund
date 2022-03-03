package katachi.spring.quizFund.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import katachi.spring.quizFund.model.Quizset;

@Repository
public interface QuizsetRepository extends JpaRepository<Quizset, Integer>, QuizsetRepositoryCustom {
	List<Quizset> findByUser(String uuid);
	Optional<Quizset> findById(int id);
	Long countByUserAndIsOpenTrue(@Param("uid") String uuid);
	@Query("select qs.isOpen from Quizset qs where qs.id = :id")
	Optional<Boolean> findIsOpenById(int id);
}
