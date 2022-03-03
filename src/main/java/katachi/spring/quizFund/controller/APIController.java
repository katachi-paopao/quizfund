package katachi.spring.quizFund.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import katachi.spring.quizFund.model.Quiz;
import katachi.spring.quizFund.model.QuizsetSummary;
import katachi.spring.quizFund.model.user.MyUserDetails;
import katachi.spring.quizFund.service.QuizService;

@RestController
public class APIController {
	final int PAGE_CONTENT = 20;

	@Autowired
	QuizService qService;

	@GetMapping("/edit/fetch")
	public Page<Quiz> getQuizListAPI(@AuthenticationPrincipal MyUserDetails user,
			@RequestParam(name="q", required=false) String query,
			@RequestParam(name="p", required=false) Integer page) {
		if (query != null && query.trim().isEmpty()) query = null;
		Page<Quiz> res = qService.fetchUserQuizPage(user.getUserId(), query, PageRequest.of(page == null ? 0 : page-1, PAGE_CONTENT));
		return res;
	}

	@PostMapping("edit/quizset/{id}/fetch")
	public Page<Quiz> postQuizSetAPI(@AuthenticationPrincipal MyUserDetails user,
			@PathVariable("id") int qsid,
			@RequestBody(required=false) Map<String, int[]> body,
			@RequestParam(name="q", required=false) String query,
			@RequestParam(name="p", required=false) Integer page) {

		List<Integer> idList = new ArrayList<Integer>();

		if (body != null) {
			for (int itemId : body.get("idList") ) {
				idList.add(itemId);
			}
		}

		if (query != null && query.trim().isEmpty()) query = null;
		if ( qService.checkAuthorOfQuizset(qsid, user.getUserId()) ) {
			return qService.fetchQuizsetEditPage(user.getUserId(), qsid, body != null ? idList : null, query, PageRequest.of(page == null ? 0 : page-1, PAGE_CONTENT));
		} else {
			return null;
		}
	}

	@GetMapping("edit/quizset/{id}/idList")
	public Map<String, Object> getQuizSetIdListAPI(@AuthenticationPrincipal MyUserDetails user,
			@PathVariable("id") int id) {
		if ( qService.checkAuthorOfQuizset(id, user.getUserId()) ) {
			return qService.findQuizsetIdList(id);
		} else {
			return null;
		}
	}

	@PostMapping("edit/delete/{targetId}")
	public Page<Quiz> postDeleteQuizAPI(@AuthenticationPrincipal MyUserDetails user,
			@PathVariable("targetId") Integer qid) {
		if (qService.checkAuthorOfQuiz(qid, user.getUserId())) {
			qService.deleteQuizById(qid);
		}

		return  getQuizListAPI(user, null, null);
	}

	@GetMapping("edit/quizset/fetch")
	public Page<QuizsetSummary> getQuizsetIndexAPI(@AuthenticationPrincipal MyUserDetails user,
			@RequestParam(name="q", required=false) String query,
			@RequestParam(name="p", required=false) Integer page) {
		if (query != null && query.trim().isEmpty()) query = null;
		Page<QuizsetSummary> res = qService.fetchUserQuizsetIndexPage(user.getUserId(), query, PageRequest.of(page == null ? 0 : page-1, PAGE_CONTENT));
		return res;
	}

	@PostMapping("/edit/quizset/open/{targetId}")
	public boolean getEditQuizsetOpenAPI(@AuthenticationPrincipal MyUserDetails user,
			@PathVariable("targetId") int qsid) {
		if (qService.checkAuthorOfQuizset(qsid, user.getUserId())) {
			return qService.openQuizsetById(qsid);
		} else {
			return false;
		}
	}

	@PostMapping("/edit/quizset/close/{targetId}")
	public boolean getEditQuizsetCloseAPI(@AuthenticationPrincipal MyUserDetails user,
			@PathVariable("targetId") int qsid) {
		if (qService.checkAuthorOfQuizset(qsid, user.getUserId())) {
			return qService.closeQuizsetById(qsid);
		} else {
			return false;
		}
	}

	@PostMapping("edit/quizset/delete/{targetId}")
	public Page<QuizsetSummary> postDeleteQuizsetAPI(@AuthenticationPrincipal MyUserDetails user,
			@PathVariable("targetId") Integer qsid) {
		if (qService.checkAuthorOfQuizset(qsid, user.getUserId())) {
			qService.deleteQuizsetById(qsid);
		}

		return  getQuizsetIndexAPI(user, null, null);
	}

	@GetMapping("game/index/fetch")
	public Page<QuizsetSummary> getGameIndexAPI(@AuthenticationPrincipal MyUserDetails user,
			@RequestParam(name="q", required=false) String query,
			@RequestParam(name="p", required=false) Integer page) {
		if (query != null && query.trim().isEmpty()) query = null;
		Page<QuizsetSummary> res = qService.fetchGameIndexPage(query, PageRequest.of(page == null ? 0 : page-1, PAGE_CONTENT));
		return res;
	}
}
