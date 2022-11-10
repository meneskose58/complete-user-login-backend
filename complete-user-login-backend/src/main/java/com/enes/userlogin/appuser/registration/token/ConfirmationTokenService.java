package com.enes.userlogin.appuser.registration.token;

import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.stereotype.Service;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ConfirmationTokenService  {
	
	private final ConfirmationTokenRepository confirmationTokenRepository;
	
	public void saveConfirmationToken(ConfirmationToken token) {
		
		confirmationTokenRepository.save(token);
	}
	
	public Optional<ConfirmationToken> getConfirmationToken(String token) {
		return confirmationTokenRepository.findByToken(token);
	}
	
	public int setConfirmedAt(String token) {
	return confirmationTokenRepository.updateConfirmedAt(token, LocalDateTime.now());
	}
	
	public String getConfirmationToken(Long appUserId){
		
		return confirmationTokenRepository.findByAppUserId(appUserId).get().getToken();
	}
	
	public boolean isTokenExpired(Long appUserId) {
		
		LocalDateTime expiredAt= confirmationTokenRepository.findByAppUserId(appUserId).get().getExpiresAt();		
		if(expiredAt.isBefore(LocalDateTime.now())) {
			
			return true;
		}
		
		return false;
	}

}
