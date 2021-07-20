package com.nagarro.accounts.security.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.nagarro.accounts.security.service.UserService;
import com.nagarro.accounts.security.utility.JWTUtility;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.nagarro.accounts.common.ApplicationConstants.*;

import java.io.IOException;


/**
 * Request filter to check authorization
 * @author claud
 *
 */
@Component
public class JwtFilter extends OncePerRequestFilter {

	private static final Logger LOGGER = LoggerFactory.getLogger(JwtFilter.class);

	private static final String BEARER_PREFIX = "Bearer ";

	private static final String AUTHORIZATION_HEADER = "Authorization";

	@Autowired
	private JWTUtility jwtUtility;

	@Autowired
	private UserService userService;

	@Override
	protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
			FilterChain filterChain) throws ServletException, IOException {
		String authorization = httpServletRequest.getHeader(AUTHORIZATION_HEADER);
		String token = null;
		String userName = null;

		try {
			LOGGER.debug("authorization = {}", authorization);
			if (null != authorization && authorization.startsWith(BEARER_PREFIX)) {
				token = authorization.substring(BEARER_PREFIX.length());
				userName = jwtUtility.getUsernameFromToken(token);
			}

			if (null != userName && SecurityContextHolder.getContext().getAuthentication() == null) {
				LOGGER.debug("userName = {}", userName);

				UserDetails userDetails = userService.loadUserByUsername(userName);

				boolean isValid = jwtUtility.validateToken(token, userDetails);
				if (isValid) {

					LOGGER.debug("userName = {} -> token validated", userName);

					UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
							userDetails, null, userDetails.getAuthorities());

					usernamePasswordAuthenticationToken
							.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));

					SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
				}

			}

			filterChain.doFilter(httpServletRequest, httpServletResponse);

		} catch (UnsupportedJwtException | MalformedJwtException jwtException) {
			LOGGER.error(jwtException.getLocalizedMessage(), jwtException);
			httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
			( httpServletResponse).sendError(HttpServletResponse.SC_FORBIDDEN,
					UNKNOWN_ERROR_PLEASE_CONTACT_THE_ADMINISTATROR);
			
		} catch (ExpiredJwtException jwtException) {
			LOGGER.error(jwtException.getLocalizedMessage(), jwtException);
			httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			(httpServletResponse).sendError(HttpServletResponse.SC_UNAUTHORIZED,
					"Token JWT expired");
		}
		
		
	}
}