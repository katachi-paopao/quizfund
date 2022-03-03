package katachi.spring.quizFund.service;

import java.util.Optional;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import katachi.spring.quizFund.model.user.Authorization;
import katachi.spring.quizFund.model.user.AuthorizationPK;
import katachi.spring.quizFund.model.user.GoogleOAuth2User;
import katachi.spring.quizFund.model.user.User;
import katachi.spring.quizFund.repository.user.AuthorizationRepository;
import katachi.spring.quizFund.repository.user.UserRepository;

@Service
@Transactional(rollbackOn = Exception.class)
public class OidcUserServiceImpl extends OidcUserService {
	@Autowired
	private AuthorizationRepository aRep;

	@Autowired
	private UserRepository uRep;

	@Override
	public OidcUser loadUser(OidcUserRequest userRequest) {
		OidcUser oidcUser = super.loadUser(userRequest);

		AuthorizationPK key = new AuthorizationPK(oidcUser.getAttribute("iss").toString(), oidcUser.getAttribute("sub"));
		Optional<Authorization> myAuth = aRep.findByKey(key);
		User user = new User();

		if(!myAuth.isPresent()) {
			user.setUuid(UUID.randomUUID().toString());
			user.setMail(oidcUser.getAttribute("email"));
			user.setName("NoName");
			uRep.saveAndFlush(user);

			Authorization newAuth = new Authorization();
			newAuth.setKey(key);
			newAuth.setUser_uuid(user.getUuid());

			aRep.saveAndFlush(newAuth);
		} else {
			user = uRep.findByUuid(myAuth.get().getUser_uuid()).get();
		}

		return new GoogleOAuth2User(user, oidcUser);
	}
}
