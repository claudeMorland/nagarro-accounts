package com.nagarro.accounts.security.service;


import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.nagarro.accounts.common.ApplicationConstants.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Generate user in memory
 * @author claud
 *
 */
@Service
public class UserService implements UserDetailsService {

	
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {

		UserDetails userDetails = null;
		
		// logic that replace user from data base
		if (StringUtils.isNotBlank(userName)) {

			if (userName.equals(USER_LOGIN)) {
				List<GrantedAuthority> authorities = new ArrayList<>();
				authorities.add(new SimpleGrantedAuthority(USER_ROLE));
				userDetails = new User(USER_LOGIN, passwordEncoder.encode(USER_LOGIN), authorities);
			}

			if (userName.equals(ADMIN_LOGIN)) {
				List<GrantedAuthority> authorities = new ArrayList<>();
				authorities.add(new SimpleGrantedAuthority(USER_ROLE));
				authorities.add(new SimpleGrantedAuthority(ADMIN_ROLE));
				userDetails = new User(ADMIN_LOGIN, passwordEncoder.encode(ADMIN_LOGIN), authorities);
			}
		}

		return userDetails;
	}
}