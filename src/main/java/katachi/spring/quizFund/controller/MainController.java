package katachi.spring.quizFund.controller;

import java.security.AccessControlException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import katachi.spring.quizFund.model.Quizset;
import katachi.spring.quizFund.model.user.MyUserDetails;
import katachi.spring.quizFund.model.validation.CreateQuizsetForm;
import katachi.spring.quizFund.model.validation.Grouping;
import katachi.spring.quizFund.model.validation.MakingTypingForm;
import katachi.spring.quizFund.model.validation.QuizsetConfigForm;
import katachi.spring.quizFund.model.validation.SignupForm;
import katachi.spring.quizFund.service.GameService;
import katachi.spring.quizFund.service.QuizService;
import katachi.spring.quizFund.service.UserService;

@Controller
public class MainController {
	@Autowired
	QuizService qService;

	@Autowired
	GameService gService;

	@Autowired
	UserService uService;

	/* ヘッダー、フッター部分読込の共通処理 */
	private void commonPreload(Model model, MyUserDetails user) {
		model.addAttribute("header", "header :: contents");
		model.addAttribute("footer", "footer :: contents");
		if (user != null) {
			model.addAttribute("quizNum", qService.quizNumByUser(user.getUserId()));
			model.addAttribute("quizsetNum", qService.openQuizsetNumByUser(user.getUserId()));
		}
	}

	/* ヘッダー、フッター部分読込の共通処理（リダイレクト用） */
	private void commonPreloadForRedirect(RedirectAttributes re, MyUserDetails user) {
		re.addFlashAttribute("header", "header :: contents");
		re.addFlashAttribute("footer", "footer :: contents");
		if (user != null) {
			re.addFlashAttribute("quizNum", qService.quizNumByUser(user.getUserId()));
			re.addFlashAttribute("quizsetNum", qService.openQuizsetNumByUser(user.getUserId()));
		}
	}

	@GetMapping({"/", "main", "home", "index"})
	public String getHome(@AuthenticationPrincipal MyUserDetails user, Model model) {
		commonPreload(model, user);
		return "home";
	}

	/* ログイン・サインアップ関連 */
	@GetMapping("login")
	public String getlogin(@AuthenticationPrincipal MyUserDetails user, Model model) {
		commonPreload(model, user);
		return "login";
	}

	@GetMapping("signup")
	public String getSignup(@AuthenticationPrincipal MyUserDetails user, Model model) {
		commonPreload(model, user);
		return "signup";
	}

	@PostMapping({"signup"})
	public String postSignup(@AuthenticationPrincipal MyUserDetails user,
			@ModelAttribute @Validated(Grouping.class) SignupForm form,
			BindingResult br, RedirectAttributes re, Model model) {
		commonPreload(model, user);
		List<String> errors = new ArrayList<String>();

		if (br.hasErrors()) {
			for (ObjectError e : br.getAllErrors()) {
				errors.add(e.getDefaultMessage());
			}
			if (errors.size() > 0) model.addAttribute("error", errors);
			return "signup";
		}
		try {
			uService.tempRegister(form);
		} catch (DuplicateKeyException e) {
			errors.add(e.getMessage());
			model.addAttribute("error", errors);
			return "signup";
		} catch (MessagingException e) {
			errors.add(e.getMessage());
			model.addAttribute("error", errors);
			return "signup";
		}

		return "auth/accept";
	}

	@GetMapping("auth/confirm/{uuid}")
	public String getRegistrationConfirm(@AuthenticationPrincipal MyUserDetails user,
			@PathVariable("uuid") String uuid, Model model) {
		commonPreload(model, user);

		if (uService.confirm(uuid) ) {
			model.addAttribute("msg", "ユーザー登録が完了しました。");
		} else {
			model.addAttribute("msg", "URLが間違っているか期限切れです。");
		}

		return "auth/completion";
	}

	/* クイズ編集機能関連 */
	@GetMapping("edit")
	public String getQuizlist(@AuthenticationPrincipal MyUserDetails user,
			Model model) {
		commonPreload(model, user);
		return "edit/quizlist";
	}

	@PostMapping("edit/register")
	public String postRegister(@AuthenticationPrincipal MyUserDetails user,
			@ModelAttribute @Validated(Grouping.class) MakingTypingForm form,
			BindingResult br, Model model, RedirectAttributes re) {

		if (user != null) {
			String uuid = user.getUserId();
			if (br.hasErrors() ) {
				List<String> errors = new ArrayList<String>();

				for (ObjectError e : br.getAllErrors()) {
					errors.add(e.getDefaultMessage());
				}

				re.addFlashAttribute("errors", errors);

			} else {
				qService.addTypingFromForm(form, uuid);
			}
		} else {
			throw new AccessControlException("not match author's ID");
		}

		commonPreloadForRedirect(re, user);
		return "redirect:/edit";
	}

	@PostMapping ("/edit/update/{id}")
	public String postUpdate(@AuthenticationPrincipal MyUserDetails user,
			@PathVariable("id") int qid,
			@ModelAttribute @Validated MakingTypingForm form,
			BindingResult br, RedirectAttributes re) {
		if (user != null) {
			String uuid = user.getUserId();
			if (qService.checkAuthorOfQuiz(qid, uuid)) {
				if (br.hasErrors() ) {
					List<String> errors = new ArrayList<String>();

					for (ObjectError e : br.getAllErrors()) {
						errors.add(e.getDefaultMessage());
					}

					re.addFlashAttribute("errors", errors);
				} else {
					qService.updateTypingFromForm(qid, form);
				}
			}
		}

		commonPreloadForRedirect(re, user);
		return "redirect:/edit";
	}

	@GetMapping("edit/quizset")
	public String getEditQuizset(@AuthenticationPrincipal MyUserDetails user,
			Model model) {
		commonPreload(model, user);
		return "edit/quizset";
	}

	@GetMapping("edit/quizset/{id}")
	public String getEditQuizsetEditor(@AuthenticationPrincipal MyUserDetails user,
			@PathVariable("id") int qsid,
			Model model) {

		model.addAttribute("qsid", qsid);
		Optional<Quizset> o_qs = qService.findQuizsetByid(qsid);
		if (o_qs.isPresent()) {
			if (o_qs.get().getUser().equals(user.getUserId())) {
				model.addAttribute("qs_name", o_qs.get().getName() );
				commonPreload(model, user);
				return "edit/quizset_editor";
			}
		}

		throw new QuizsetNotFoundException();

	}

	@PostMapping("edit/quizset/create")
	public String postCreateQuizset (@AuthenticationPrincipal MyUserDetails user,
			@ModelAttribute @Validated CreateQuizsetForm form,
			BindingResult br, RedirectAttributes re) {
		if (!br.hasErrors()) {
			qService.createQuizsetFromForm(user.getUserId(), form);
		}
		commonPreloadForRedirect(re, user);
		return "redirect:/edit/quizset";
	}

	@PostMapping("edit/quizset/config/{targetId}")
	public String postQuizsetConfig (@AuthenticationPrincipal MyUserDetails user,
			@PathVariable("targetId") Integer targetId,
			@ModelAttribute @Validated QuizsetConfigForm form,
			BindingResult br,
			RedirectAttributes re) {
		qService.checkAuthorOfQuizset(targetId, user.getUserId());
		if (!br.hasErrors()) {
			qService.resetConfigQuizsetById(targetId, form);
		}
		commonPreloadForRedirect(re, user);
		return "redirect:/edit/quizset/" + targetId;
	}

	@PostMapping("edit/quizset/update/{targetId}")
	public String postQuizsetUpdate (@AuthenticationPrincipal MyUserDetails user,
			@PathVariable("targetId") Integer targetId,
			@RequestParam Map<String, String> body,
			RedirectAttributes re) {
		String uuid = user.getUserId();
		qService.checkAuthorOfQuizset(targetId, uuid);
		List<Integer> list = new ArrayList<Integer>();

		String[] split = body.get("list").split(",");
		if (!split[0].equals("")) {
			try {
				for (String id : body.get("list").split(",")) {
					int iid = Integer.parseInt(id);
					if (qService.checkAuthorOfQuiz(iid, uuid)) {
						list.add(iid);
					} else {
						throw new AccessControlException("not match author's ID");
					};
				}
			} catch (NumberFormatException e) {
				re.addFlashAttribute("error", "編集の保存に失敗しました");
				return "redirect:/edit/quizset/" + targetId;
			} catch (AccessControlException e) {
				re.addFlashAttribute("error", "編集の保存に失敗しました");
				return "redirect:/edit/quizset/" + targetId;
			}
		}
		qService.resetQuizset(targetId, list);
		commonPreloadForRedirect(re, user);
		return "redirect:/edit/quizset/" + targetId;
	}

	/* ゲーム機能関連 */
	@GetMapping("game")
	public String getGameIndex(@AuthenticationPrincipal MyUserDetails user,
			Model model) {
		commonPreload(model, user);
		return "game/index";
	}

	@GetMapping("/game/play/{id}")
	public String getGameMain(@AuthenticationPrincipal MyUserDetails user,
			@PathVariable("id") int id,
			Model model) {
		commonPreload(model, user);
		return "game/main";
	}

	@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Quizset Not Found")
	public class QuizsetNotFoundException extends RuntimeException {
	}
}
