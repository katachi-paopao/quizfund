package katachi.spring.quizFund.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import javax.mail.MessagingException;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import katachi.spring.quizFund.model.user.User;
import katachi.spring.quizFund.model.user.UserTemp;
import katachi.spring.quizFund.model.validation.SignupForm;
import katachi.spring.quizFund.repository.user.UserRepository;
import katachi.spring.quizFund.repository.user.UserTempRepository;

@Service
public class UserService {
	@Autowired
	UserTempRepository userTmpRep;

	@Autowired
	UserRepository userRep;

	@Autowired
	PasswordEncoder passwordEncoder;

	@Autowired
	MailService mailService;

	@Transactional(rollbackOn = Exception.class)
	public void tempRegister(SignupForm form) throws MessagingException {
		if (!userRep.findByMail(form.getMail()).isPresent()) {
			Optional<UserTemp> o_utmp = userTmpRep.findByMail(form.getMail());
			UserTemp user;

			if (o_utmp.isPresent()) {
				user = o_utmp.get();
				user.setPassword(passwordEncoder.encode(form.getPassword()));

				Date d = new Date();

				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				user.setCreated_on(sdf.format(d));
			} else {

				user = new UserTemp();
				user.setUuid(UUID.randomUUID().toString());
				user.setMail(form.getMail());
				user.setPassword(passwordEncoder.encode(form.getPassword()));
				user.setName("No Name");

				Date d = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				user.setCreated_on(sdf.format(d));
			}

			userTmpRep.saveAndFlush(user);

			mailService.sendConfirmMail(user.getMail(), user.getUuid());
		} else {
			throw new DuplicateKeyException("入力したメールアドレスは既に登録されています");
		}
	}

	@Transactional(rollbackOn = Exception.class)
	public boolean confirm(String uuid) {
		UserTemp user_t = userTmpRep.findByUuid(uuid);
		if (user_t != null) {
			User user = new User(user_t.getUuid(), user_t.getMail(), user_t.getPassword(), user_t.getName());
			userRep.saveAndFlush(user);
			userTmpRep.delete(user_t);
			return true;
		}
		else return false;
	}
}
