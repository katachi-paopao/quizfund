package katachi.spring.quizFund.model.validation;

import javax.validation.GroupSequence;

import katachi.spring.quizFund.model.validation.Grouping.First;
import katachi.spring.quizFund.model.validation.Grouping.Second;
import katachi.spring.quizFund.model.validation.Grouping.Third;

@GroupSequence({First.class, Second.class, Third.class})
public interface Grouping {
	 public interface First {
	  }

	  public interface Second {
	  }

	  public interface Third {
	  }
}
