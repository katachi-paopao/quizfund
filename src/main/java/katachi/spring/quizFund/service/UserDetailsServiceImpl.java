package katachi.spring.quizFund.service;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import katachi.spring.quizFund.model.user.User;
import katachi.spring.quizFund.model.user.UserDetailsImpl;
import katachi.spring.quizFund.repository.user.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService{
	@Autowired
	UserRepository uRep;

	@Transactional
	@Override
	public UserDetails loadUserByUsername(String mail) throws UsernameNotFoundException {
		Optional<User> user = uRep.findByMail(mail);
		if (user.isPresent()) {
			return new UserDetailsImpl(user.get());
		} else {
			throw new UsernameNotFoundException("No Such User's Address: " + mail);
		}
	}
}
