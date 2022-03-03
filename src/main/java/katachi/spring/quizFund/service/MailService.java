package katachi.spring.quizFund.service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class MailService {
	@Autowired
	JavaMailSender mailSender;

	public void sendConfirmMail(String to, String uuid) throws MessagingException {
		MimeMessage mm = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mm, "utf-8");
		helper.setTo(to);
		helper.setSubject("【QuizFund】ユーザー登録確認用メール");
		helper.setText("本サイトへの仮登録が完了しました。\n以下のリンクにアクセスして登録を完了してください。\n"
				+ "<a href='http://localhost:8080/auth/confirm/" + uuid +"'>"
				+ "http://localhost:8080/auth/confirm/" + uuid + "</a>", true);
		mailSender.send(mm);
	}
}
