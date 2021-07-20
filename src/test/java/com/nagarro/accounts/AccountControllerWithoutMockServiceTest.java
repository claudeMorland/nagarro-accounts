package com.nagarro.accounts;

import static com.nagarro.accounts.common.ApplicationConstants.ADMIN_LOGIN;
import static com.nagarro.accounts.common.ApplicationConstants.ADMIN_ROLE;
import static com.nagarro.accounts.common.ApplicationConstants.USER_LOGIN;
import static com.nagarro.accounts.common.ApplicationConstants.USER_ROLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.time.LocalDate;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

import com.nagarro.accounts.controller.AccountsController;
import com.nagarro.accounts.error.functional.AccountBadRequestException;
import com.nagarro.accounts.error.functional.ResourceNotFoundException;
import com.nagarro.accounts.security.config.SecurityConfiguration;


@AutoConfigureMockMvc
@EnableConfigurationProperties
@SpringBootTest
@ActiveProfiles("test")
@Import(value = {SecurityConfiguration.class})
public class AccountControllerWithoutMockServiceTest {
	

    @Inject
    private AccountsController accountsController;


	@Test
	@WithMockUser(username = USER_LOGIN, password = USER_LOGIN, authorities = {USER_ROLE})
	public void getStatementByDateRangeForbidden() throws Exception {
		
		try {
			accountsController.getStatements(3, LocalDate.now().minusYears(10), LocalDate.now(), null, null);
			fail();
		} catch (Exception exception) {
			assertTrue(exception instanceof AccessDeniedException);
		}
	}
	
	/**
	 * Exeption reach if no result
	 * @throws Exception
	 */
	@Test
	@WithMockUser(username = ADMIN_LOGIN, password = ADMIN_LOGIN, authorities = { ADMIN_ROLE })
	public void getStatementByDateRangeNotFound() throws Exception {
		
		try {
			accountsController.getStatements(3, LocalDate.now(), LocalDate.now(), null, null);
			fail();
		} catch (Exception exception) {
			assertTrue(exception instanceof ResourceNotFoundException);
		}
	}
	
	@Test
	@WithMockUser(username = ADMIN_LOGIN, password = ADMIN_LOGIN, authorities = { ADMIN_ROLE })
	public void getStatementByDateRangeFound() throws Exception {
		
		try {
			ResponseEntity<Object> result = accountsController.getStatements(3, LocalDate.now().minusYears(10), LocalDate.now(), null, null);
			assertEquals(HttpStatus.OK, result.getStatusCode());
		} catch (Exception exception) {
			fail();
		}
	}
	
	@Test
	@WithMockUser(username = ADMIN_LOGIN, password = ADMIN_LOGIN, authorities = { ADMIN_ROLE })
	public void getStatementByDateRangeIncompatible() throws Exception {
		
		try {
			accountsController.getStatements(3, null, LocalDate.now(), null, null);
			fail();
		} catch (Exception exception) {
			assertTrue(exception instanceof AccountBadRequestException);
		}
	}
	
	@Test
	@WithMockUser(username = ADMIN_LOGIN, password = ADMIN_LOGIN, authorities = { ADMIN_ROLE })
	public void getStatementByAmountRangeIncompatible() throws Exception {
		
		try {
			accountsController.getStatements(3, null, null, null, 30);
			fail();
		} catch (Exception exception) {
			assertTrue(exception instanceof AccountBadRequestException);
		}
	}
	
	
}