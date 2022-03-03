package katachi.spring.quizFund.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import katachi.spring.quizFund.model.IntermediateQuizAndQuizset;
import katachi.spring.quizFund.model.IntermediateQuizAndQuizsetPK;
import katachi.spring.quizFund.model.Quiz;
import katachi.spring.quizFund.model.Quizset;
import katachi.spring.quizFund.model.QuizsetSummary;
import katachi.spring.quizFund.model.TypingAnswer;
import katachi.spring.quizFund.model.TypingOption;
import katachi.spring.quizFund.model.validation.CreateQuizsetForm;
import katachi.spring.quizFund.model.validation.MakingTypingForm;
import katachi.spring.quizFund.model.validation.QuizsetConfigForm;
import katachi.spring.quizFund.repository.IntermediateQuizAndQuizsetRepository;
import katachi.spring.quizFund.repository.QuizRepository;
import katachi.spring.quizFund.repository.QuizsetRepository;
import katachi.spring.quizFund.repository.TypingAnswerRepository;
import katachi.spring.quizFund.repository.TypingOptionRepository;

@Service
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class QuizService {
	@Autowired
	QuizRepository qRep;
	@Autowired
	TypingAnswerRepository typingAnsRep;
	@Autowired
	TypingOptionRepository typingOptRep;
	@Autowired
	QuizsetRepository qsRep;
	@Autowired
	IntermediateQuizAndQuizsetRepository intRep;

	public long quizNumByUser(String uuid) {
		return qRep.countByUser(uuid);
	}

	public long openQuizsetNumByUser(String uuid) {
		return qsRep.countByUserAndIsOpenTrue(uuid);
	}

	public Optional<Quiz> fetchQuiz() {
		Long n = qRep.count();
		int index = (int)(Math.random()*n);
		Page<Quiz> p = qRep.findAll(PageRequest.of(index, 1));
		Optional<Quiz> q = Optional.empty();
		if (p.hasContent()) {
			q = Optional.of(p.getContent().get(0));
		}
		System.out.println(q.toString());
		return q;
	}

	public List<Quiz> fetchUserQuizList(String uuid) {
		return qRep.findByUser(uuid);
	}

	public Page<Quiz> fetchUserQuizPage(String uuid, String query, Pageable pageable) {
		return qRep.findByUserPage(uuid, query, pageable);
	}

	public Page<QuizsetSummary> fetchUserQuizsetIndexPage(String uuid, String query, Pageable pageable) {
		return qsRep.createQuizsetIndexPage(uuid, query, pageable);
	}

	public Page<Quiz> fetchQuizsetEditPage(String uuid, int qsid, List<Integer> exIds, String query, Pageable pageable) {
		return qsRep.createQuizsetPage(uuid, qsid, exIds, query, pageable);
	}

	public List<Quizset> fetchUserQuizsetList(String uuid) {
		return qsRep.findByUser(uuid);
	}

	public Optional<Quiz> findQuizById(int qid) {
		return qRep.findById(qid);
	}

	public void deleteQuizById(int qid) {
		qRep.deleteById(qid);
		return;
	}

	public Optional<Quizset> findQuizsetByid(int qsid) {
		return qsRep.findByIdCustom(qsid);
	}

	public boolean CheckQuizsetIsOpen(int qsid) {
		Optional<Boolean> o_b = qsRep.findIsOpenById(qsid);

		if (o_b.isPresent()) {
			return o_b.get();
		} else {
			return false;
		}
	}

	public Optional<Quizset> findOpenQuizsetByid(int qsid) {
		Optional<Quizset> o_qs = qsRep.findById(qsid);

		if (o_qs.isPresent() ){
			Quizset qs = o_qs.get();
			/* 問題セットが公開されているかを調べる */
			if (qs.isOpen() ) {
				List<Integer> idList = intRep.findQuizIdByKeyQuizsetIdCustom(qsid, qs.isRandom(), qs.getNum());
				List<Quiz> quizList = qRep.findByIdInCustom(idList);
				qs.setQuizList(quizList);

				return Optional.of(qs);
			}
		}

		return Optional.empty();
	}

	public void deleteQuizsetById(int qsid) {
		qsRep.deleteById(qsid);
		return;
	}

	public Map<String, Object> findQuizsetIdList(int qsid) {
		return qsRep.findAllQuizIdByQuizsetId(qsid);
	}

	public boolean checkAuthorOfQuiz(int qid, String uuid) {
		Optional<Quiz> q = qRep.findById(qid);
		if (q.isPresent()) {
			return q.get().getUser().equals(uuid);
		} else {
			return false;
		}
	}

	public boolean checkAuthorOfQuizset(int qsid, String uuid) {
		Optional<Quizset> q = qsRep.findById(qsid);
		if (q.isPresent()) {
			return q.get().getUser().equals(uuid);
		} else {
			return false;
		}
	}

	public boolean checkAnswer(String user_answer) {
		List<TypingAnswer> ans_list = typingAnsRep.findAllByKeyId(2);
		for (TypingAnswer ans : ans_list) {
			if (user_answer.equals(ans.getAnswer())) return true;
		}
		return false;
	}

	/* game */
	public Page<QuizsetSummary> fetchGameIndexPage(String query, Pageable pageable) {
		return qsRep.createGameIndexPage(query, pageable);
	}

	@Transactional(rollbackOn = Exception.class)
	public void addTypingFromForm(MakingTypingForm form, String uuid) {
		Quiz quiz = new Quiz(0, form.getText(), uuid);
		quiz = qRep.saveAndFlush(quiz);

		TypingOption option = new TypingOption(quiz.getId(), form.isArbitrary() ? null : form.getAnswer().length(), form.getAns_type());
		typingOptRep.saveAndFlush(option);

		TypingAnswer answer = new TypingAnswer(quiz.getId(), 0, form.getAnswer());
		typingAnsRep.saveAndFlush(answer);

		for (int i=0; i<3; i++) {
			String str =  form.getAlt_answers()[i];
			if (str.length() > 0) {
				answer = new TypingAnswer(quiz.getId(), i+1, form.getAlt_answers()[i]);
				typingAnsRep.saveAndFlush(answer);
			}
		}
	}

	@Transactional(rollbackOn = Exception.class)
	public void updateTypingFromForm(int qid, MakingTypingForm form) {
		Optional<Quiz> q = qRep.findById(qid);
		if (q.isPresent()) {
			/* quiz テーブルの更新処理 */
			Quiz qdata = q.get();
			qdata.setText(form.getText());
			qdata.setAnswers(null);
			qRep.saveAndFlush(qdata);

			/* typing_answer テーブルの更新処理 */
			typingAnsRep.deleteAllByKeyId(qid);
			typingAnsRep.flush();

			TypingAnswer answer = new TypingAnswer(qdata.getId(), 0, form.getAnswer());
			typingAnsRep.saveAndFlush(answer);

			for (int i=0; i<3; i++) {
				String str =  form.getAlt_answers()[i];
				if (str.length() > 0) {
					answer = new TypingAnswer(qdata.getId(), i+1, str);
					typingAnsRep.saveAndFlush(answer);
				}
			}

			/* typing_option テーブルの更新処理 */
			typingOptRep.deleteAllById(qid);
			typingOptRep.flush();
			TypingOption option = new TypingOption(qdata.getId(), form.isArbitrary() ? null : form.getAnswer().length(), form.getAns_type());
			typingOptRep.saveAndFlush(option);

		}
	}

	@Transactional(rollbackOn = Exception.class)
	public void updatePlayCount(int qsid) {
		Optional<Quizset> o_qs = qsRep.findById(qsid);
		if (o_qs.isPresent()) {
			Quizset qs = o_qs.get();
			qs.setPlayCount(qs.getPlayCount() + 1);
		}
	}

	@Transactional(rollbackOn = Exception.class)
	public void updateCorrectRate(int qid, boolean isCorrect) {
		Optional<Quiz> o = qRep.findById(qid);
		if (o.isPresent()) {
			Quiz q = o.get();
			q.setAppearance(q.getAppearance()+1);
			if (isCorrect) q.setCorrect(q.getCorrect()+1);
			qRep.saveAndFlush(q);
		}
	}

	@Transactional(rollbackOn = Exception.class)
	public void resetQuizset(int qsid, List<Integer> targetIdList) {
		intRep.deleteAllByKeyQuizsetIdCustom(qsid);
		intRep.flush();

		int serial = 0;
		for (Integer qid : targetIdList) {
			intRep.saveAndFlush(new IntermediateQuizAndQuizset
				(new IntermediateQuizAndQuizsetPK(qsid, qid), serial++
			));
		}

		Quizset qs = qsRep.findById(qsid).get();
		Long maxNum = intRep.countByKeyQuizsetId(qsid);
		if (qs.getNum() > maxNum) {
			qs.setNum(Math.toIntExact(maxNum));
		}

		if (qs.getNum() == 0) {
			qs.setOpen(false);
		}
	}

	@Transactional(rollbackOn = Exception.class)
	public void removeItemFromQuizset(int qsid, List<Integer> targetIdList) {
		for (Integer qid : targetIdList) {
			intRep.deleteByKey(new IntermediateQuizAndQuizsetPK(qsid, qid));
		}
	}

	@Transactional(rollbackOn = Exception.class)
	public void createQuizsetFromForm(String uid, CreateQuizsetForm form) {
		Quizset newQuizset = new Quizset();
		newQuizset.setName(form.getName());
		newQuizset.setIntroduction(form.getIntro());
		newQuizset.setUser(uid);
		qsRep.saveAndFlush(newQuizset);
	}

	@Transactional(rollbackOn = Exception.class)
	public void resetConfigQuizsetById(int qsid, QuizsetConfigForm form) {
		if (form.getGameNum() == 0 && CheckQuizsetIsOpen(qsid)) {
			throw new IllegalArgumentException("公開中のクイズセットの出題数を0にすることはできません");
		}

		Optional<Quizset> o_qs = qsRep.findById(qsid);
		if (o_qs.isPresent()) {
			Quizset qs = o_qs.get();
			qs.setName(form.getName());
			qs.setIntroduction(form.getIntro());
			qs.setRandom(form.isRandom());
			qs.setNum(form.getGameNum() < qs.getQuizList().size() ? form.getGameNum() : qs.getQuizList().size());
			qsRep.saveAndFlush(o_qs.get());
		}
	}

	@Transactional(rollbackOn = Exception.class)
	public void changeQuizsetIntroById(int qsid, String text) {
		Optional<Quizset> o_qs = qsRep.findById(qsid);
		if (o_qs.isPresent()) {
			o_qs.get().setIntroduction(text);
			qsRep.saveAndFlush(o_qs.get());
		}
	}

	@Transactional(rollbackOn = Exception.class)
	public boolean openQuizsetById(int qsid) {
		Optional<Quizset> o_qs = qsRep.findById(qsid);

		if (o_qs.isPresent()) {
			if (o_qs.get().getNum() == 0) {
				return false;
			}

			o_qs.get().setOpen(true);
			qsRep.saveAndFlush(o_qs.get());
			return true;
		} else {
			return false;
		}
	}

	@Transactional(rollbackOn = Exception.class)
	public boolean closeQuizsetById(int qsid) {
		Optional<Quizset> o_qs = qsRep.findById(qsid);
		if (o_qs.isPresent()) {
			o_qs.get().setOpen(false);
			qsRep.saveAndFlush(o_qs.get());
			return true;
		} else {
			return false;
		}
	}
}
