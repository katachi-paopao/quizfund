package katachi.spring.quizFund.model.validation;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

import katachi.spring.quizFund.model.validation.Grouping.First;
import katachi.spring.quizFund.model.validation.Grouping.Second;
import lombok.Data;

@Data
public class SignupForm {
	@NotBlank(message = "メールアドレスを入力してください", groups=First.class)
	@Email(message = "メールアドレスの書式が正しくありません", groups=Second.class)
	private String mail;

	@NotBlank(message = "パスワードを入力してください", groups=First.class)
	@Pattern(regexp="^[a-zA-Z0-9]+$", message="パスワードは英数字のみ使用できます", groups=Second.class)
	@Length(min=8, max=64, message="パスワードは8文字以上、64文字以下にしてください", groups=Second.class)
	private String password;

	@NotBlank(message = "確認用パスワードを入力してください", groups=First.class)
	private String confirmPassword;

	@AssertTrue(message = "入力されたパスワードと確認用パスワードが異なります", groups=Second.class)
	public boolean isSamePassword() {
		return this.confirmPassword.equals(password);
	}
}
