package com.enes.userlogin.appuser.registration;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class RegistrationRequest {
	
	private  String firstName;
	private  String lastName;
	private  String email;
	private  String password;
	
}
