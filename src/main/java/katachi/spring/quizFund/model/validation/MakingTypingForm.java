package katachi.spring.quizFund.model.validation;
import java.util.regex.Matcher;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Length;

import katachi.spring.quizFund.model.validation.Grouping.First;
import katachi.spring.quizFund.model.validation.Grouping.Second;
import katachi.spring.quizFund.model.validation.Grouping.Third;
import lombok.Data;

@Data
public class MakingTypingForm {
	@NotBlank(message = "問題文を入力してください",  groups=First.class)
	@Length(max=200, message = "問題文は200文字以内で入力してください",  groups=Second.class)
	private String text;

	@NotBlank(message = "解答を入力してください",  groups=First.class)
	@Length(max=12, message = "解答は12文字以内で入力してください", groups=Second.class)
	@Pattern(regexp="(^[\\u3040-\\u309F]+$)|(^[\\u30A0-\\u30FF]+$)|(^[Ａ-Ｚ]+$)|(^[０-９]+$)",
	message="解答の文字種はひらがな、カタカナ、全角英大文字、全角数字のいずれか1種類のみ使用できます", groups=Third.class)
	private String answer;

	@Size(max=3)
	private String[] alt_answers;

	private boolean arbitrary;

	private int ans_type;

	@AssertTrue(message = "文字列長は複数の解答で同じにしてください", groups=Third.class)
	public boolean isSameAnsLength() {
		if (arbitrary) return true;
		int strLen = answer.length();
		for (int i=0, len=alt_answers.length; i<len; i++) {
			if (alt_answers[i].length() != 0 && alt_answers[i].length() != strLen) return false;
		}
		return true;
	}

	@AssertTrue(message = "文字種は複数の解答で同じにしてください（ひらがな、カタカナ、全角英大文字、全角数字）", groups=Third.class)
	public boolean isSameAnsType() {
		ans_type = -1;
		java.util.regex.Pattern[] p = new java.util.regex.Pattern [4];
		p[0] = java.util.regex.Pattern.compile("^[\\u3040-\\u309F|ー]+$");
		p[1] = java.util.regex.Pattern.compile("^[\\u30A0-\\u30FF]+$");
		p[2] = java.util.regex.Pattern.compile("^[Ａ-Ｚ]+$");
		p[3] = java.util.regex.Pattern.compile("^[０-９]+$");
		for (int i=0; i<4; i++) {
			Matcher matcher = p[i].matcher(answer);
			if (matcher.matches()) {
				ans_type = i;
				break;
			}
		}
		if (ans_type == -1) return false;

		for (int i=0, len=alt_answers.length; i<len; i++) {
			if (alt_answers[i].length() == 0) continue;
			Matcher matcher = p[ans_type].matcher(alt_answers[i]);
			if (matcher.matches() == false) return false;
		}
		return true;
	}
}