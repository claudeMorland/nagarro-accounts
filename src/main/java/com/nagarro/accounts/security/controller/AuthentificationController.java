package com.nagarro.accounts.security.controller;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.nagarro.accounts.error.functional.AuthentificationBadRequestException;
import com.nagarro.accounts.error.functional.UserNotFoundRequestException;
import com.nagarro.accounts.security.model.JwtRequest;
import com.nagarro.accounts.security.model.JwtResponse;
import com.nagarro.accounts.security.service.UserService;
import com.nagarro.accounts.security.utility.JWTUtility;

/**
 * Rest Api for authentification
 * Allow to get token
 * @author claud
 *
 */
@Validated
@RestController
public class AuthentificationController {

	private static final Logger LOGGER = LoggerFactory.getLogger(AuthentificationController.class);

	@Autowired
	private JWTUtility jwtUtility;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private UserService userService;

	@PostMapping("/authenticate")
	public JwtResponse authenticate(@Valid @RequestBody JwtRequest jwtRequest) throws BadCredentialsException {

		LOGGER.debug("authenticate {} start ...", jwtRequest);
		
		if (jwtRequest == null) {
			throw new AuthentificationBadRequestException("Invalid request param");
		}
		
		final UserDetails userDetails = this.userService.loadUserByUsername(jwtRequest.getUsername());

		if (userDetails == null) {
			throw new UserNotFoundRequestException("Unknow user");
		}
		this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(jwtRequest.getUsername(),
				jwtRequest.getPassword(), userDetails.getAuthorities()));

		final String token = this.jwtUtility.generateToken(userDetails);
		
		return new JwtResponse(token);
	}
}