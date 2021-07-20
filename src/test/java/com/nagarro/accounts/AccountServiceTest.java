package com.nagarro.accounts;

import static com.nagarro.accounts.common.ApplicationConstants.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

import com.nagarro.accounts.persistence.entity.Statement;
import com.nagarro.accounts.security.config.SecurityConfiguration;
import com.nagarro.accounts.security.filter.JwtFilter;
import com.nagarro.accounts.security.service.UserService;
import com.nagarro.accounts.services.AccountsService;


@AutoConfigureMockMvc
@EnableConfigurationProperties
@SpringBootTest
@ActiveProfiles("test")
@Import(value = {SecurityConfiguration.class})
public class AccountServiceTest {
	
	private static final DateTimeFormatter FORMATTER_API = DateTimeFormatter.ofPattern(DATE_FORMAT_API);
	

	@Inject
	private AccountsService accountsService;
	
	@MockBean
	private UserService userService;
	
	@MockBean
	private JwtFilter jwtFilter;
		

	@Test
	@WithMockUser(username = ADMIN_LOGIN, password = ADMIN_LOGIN, authorities = {ADMIN_ROLE})
	public void getStatementByAmountRangeLarge() throws Exception {
		List<Statement> statements = this.accountsService.getStatementByAmountRange(3, 1, 10000);
		assertEquals("Number total of statement for account 3", 30, statements.size());
	}
	
	@Test
	@WithMockUser(username = ADMIN_LOGIN, password = ADMIN_LOGIN, authorities = {ADMIN_ROLE})
	public void getStatementByAmountRangeInf100() throws Exception {
		List<Statement> statements = this.accountsService.getStatementByAmountRange(3, 0, 100);
		assertEquals("Number total of statement for account 3", 3, statements.size());
	}
	
	@Test
	@WithMockUser(username = ADMIN_LOGIN, password = ADMIN_LOGIN, authorities = {ADMIN_ROLE})
	public void getStatementByAmountRangeNoResult() throws Exception {
		List<Statement> statements = this.accountsService.getStatementByAmountRange(3, 1000, 1002);
		assertEquals("Number total of statement for account", 0, statements.size());
	}
	
	/**
	 * Acces interdit pour les profiles user
	 */
	@Test
	@WithMockUser(username = USER_LOGIN, password = USER_LOGIN, authorities = {USER_ROLE})
	public void getStatementByAmountRangeUserForbidden() {
		try {
			this.accountsService.getStatementByAmountRange(3, 0, 100);
			fail();
		} catch (Exception accessException) {
			assertTrue(accessException instanceof AccessDeniedException);
		}
	}
	
	/**
	 * Acces interdit pour les profiles user
	 */
	@Test
	@WithMockUser(username = USER_LOGIN, password = USER_LOGIN, authorities = {USER_ROLE})
	public void getStatementByDateRangeUserForbidden() {
		try {
			this.accountsService.getStatementByDateRange(3, LocalDate.now(), LocalDate.now());
			fail();
		} catch (Exception accessException) {
			assertTrue(accessException instanceof AccessDeniedException);
		}
	}
	
	
	@Test
	@WithMockUser(username = ADMIN_LOGIN, password = ADMIN_LOGIN, authorities = {ADMIN_ROLE})
	public void getStatementLastMonthsAdmin() {
			List<Statement> statements = this.accountsService.getStatementLastMonths(3);
			assertEquals("Number total of statement for account 3", 2, statements.size());
	}
	
	//TODO test to modify since it move according the date
	@Test
	@WithMockUser(username = USER_LOGIN, password = USER_LOGIN, authorities = {USER_ROLE})
	public void getStatementLastMonthsUser() {
			List<Statement> statements = this.accountsService.getStatementLastMonths(3);
			assertEquals("Number total of statement for account 3", 2, statements.size());
	}
	
	@Test
	@WithMockUser(username = USER_LOGIN, password = USER_LOGIN, authorities = {USER_ROLE})
	public void getStatementLastMonthsNotFound() {
			List<Statement> statements = this.accountsService.getStatementLastMonths(4);
			assertEquals("Number total of statement for account 4", 0, statements.size());
	}
	
	
	@Test
	@WithMockUser(username = ADMIN_LOGIN, password = ADMIN_LOGIN, authorities = {ADMIN_ROLE})
	public void getStatementByDateRangeNoResult() {
		List<Statement> statements = this.accountsService.getStatementByDateRange(3, LocalDate.now(), LocalDate.now());
			assertEquals("Number total of statement for account 4", 0, statements.size());
	}
	
	@Test
	@WithMockUser(username = ADMIN_LOGIN, password = ADMIN_LOGIN, authorities = {ADMIN_ROLE})
	public void getStatementByDateRangeLarge() {
		List<Statement> statements = this.accountsService.getStatementByDateRange(3, LocalDate.now().minusYears(10), LocalDate.now());
			assertEquals("Number total of statement for account", 30, statements.size());
	}
	
	
	@Test
	@WithMockUser(username = ADMIN_LOGIN, password = ADMIN_LOGIN, authorities = {ADMIN_ROLE})
	public void getStatementByDateRangeSmall() {
		//17.01.2012 => 27.04.2012 
		LocalDate dateStart = LocalDate.parse("2012-01-17", FORMATTER_API);
		LocalDate dateEnd = LocalDate.parse("2012-04-27", FORMATTER_API);
		List<Statement> statements = this.accountsService.getStatementByDateRange(3, dateStart, dateEnd);
			assertEquals("Number total of statement for account", 6, statements.size());
	}
	
	
	@Test
	@WithMockUser(username = ADMIN_LOGIN, password = ADMIN_LOGIN, authorities = {ADMIN_ROLE})
	public void getStatementByDateRangeEqual() {
		//24.01.2021 => 24.01.2021  
		LocalDate dateStart = LocalDate.parse("2021-01-24", FORMATTER_API);
		LocalDate dateEnd = LocalDate.parse("2021-01-24", FORMATTER_API);
		List<Statement> statements = this.accountsService.getStatementByDateRange(3, dateStart, dateEnd);
			assertEquals("Number total of statement for account", 1, statements.size());
	}
	
	
	
	
}