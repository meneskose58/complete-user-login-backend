package com.enes.userlogin.appuser;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.enes.userlogin.appuser.registration.token.ConfirmationToken;
import com.enes.userlogin.appuser.registration.token.ConfirmationTokenService;
import com.enes.userlogin.security.PasswordEncoder;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AppUserService implements UserDetailsService {

	private final static String USER_NOT_FOUND_MSG = "User with email %s not found";
	private final static Long TOKEN_EXPIRATION_MINUTE=1L;
	private final AppUserRepository appUserRepository;
	private final PasswordEncoder passwordEncoder;
	private final ConfirmationTokenService confirmationTokenService;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
		return appUserRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException(String.format(USER_NOT_FOUND_MSG, email)));
	}
	
	@Transactional
	public String signUpUser(AppUser appUser) {
		

		boolean userExist = appUserRepository.findByEmail(appUser.getEmail()).isPresent();

		if (userExist) {
			
			boolean userEnabled=appUserRepository.findByEmail(appUser.getEmail()).get().isEnabled();
			
			if(userEnabled) {
				throw new IllegalStateException(String.format("User: %s already exist", appUser.getEmail()));	
			}
			
			else {
				
				if(confirmationTokenService.isTokenExpired(appUserRepository.findByEmail(appUser.getEmail()).get().getId())) {
					
					AppUser appUserFromDb= appUserRepository.findByEmail(appUser.getEmail()).get();
					String token = UUID.randomUUID().toString();
					ConfirmationToken confirmationToken = new ConfirmationToken(token, LocalDateTime.now(),
					LocalDateTime.now().plusMinutes(TOKEN_EXPIRATION_MINUTE), appUserFromDb);

					confirmationTokenService.saveConfirmationToken(confirmationToken);
					
					return token;
				}
				
				else {
					
					throw new IllegalStateException(String.format("User: %s already have non-expired token: %s", appUser.getEmail(), 
							confirmationTokenService.getConfirmationToken(appUserRepository.findByEmail(appUser.getEmail()).get().getId())));
				}
			}
		}
		

		
		else {
			
			String encodedPassword = passwordEncoder.encode(appUser.getPassword());
			appUser.setPassword(encodedPassword);
			appUserRepository.save(appUser);

			// TODO: Send confirmation Token
			
			String token = UUID.randomUUID().toString();
			ConfirmationToken confirmationToken = new ConfirmationToken(token, LocalDateTime.now(),
			LocalDateTime.now().plusMinutes(TOKEN_EXPIRATION_MINUTE), appUser);

			confirmationTokenService.saveConfirmationToken(confirmationToken);

			return token;
		}
		
	}
	
	public int enableAppUser(String email) {
        return appUserRepository.enableAppUser(email);
    }

}
