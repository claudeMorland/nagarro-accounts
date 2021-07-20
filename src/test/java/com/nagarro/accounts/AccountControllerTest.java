package com.nagarro.accounts;

import static com.nagarro.accounts.common.ApplicationConstants.*;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.nagarro.accounts.controller.AccountsController;
import com.nagarro.accounts.persistence.entity.Statement;
import com.nagarro.accounts.security.config.SecurityConfiguration;
import com.nagarro.accounts.services.AccountsService;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@WebMvcTest(value = AccountsController.class)
@Import(SecurityConfiguration.class)
public class AccountControllerTest {
	
	@Configuration
	@ComponentScan("com.nagarro.accounts")
    public static class TestConfiguration {
    }
	
    @InjectMocks
    private AccountsController accountsController;

  
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private AccountsService accountsService;
	
	@MockBean
	private JdbcTemplate jdbcTemplate;
	
	private Statement mockStatement = new Statement(1, 3, "16.07.2020", "320.113318991709", "current", "0012250016001");
	

	@Test
	@WithMockUser(username = USER_LOGIN, password = USER_LOGIN, roles = { USER_ROLE })
	public void pingDefaultMessage() throws Exception {
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/accounts/ping").accept(MediaType.APPLICATION_JSON);

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		System.out.println(result.getResponse());
		String expected = "Application acccounts is running";
		
		assertTrue(result.getResponse()
				.getContentAsString().contains(expected));
	}
	
	@Test
	@WithMockUser(username = USER_LOGIN, password = USER_LOGIN, roles = { USER_ROLE })
	public void getStatementUserSuccess() throws Exception {
		
		
		when(accountsService.getStatementLastMonths(Mockito.anyInt())).thenReturn(Arrays.asList(mockStatement));
		
		String myTasks = "[{\"id\":1,\"accountId\":3,\"date\":\"16.07.2020\",\"amount\":\"320.113318991709\"}]";

        mockMvc.perform(get(String.format("/accounts/%s/statements", 3))).andExpect(status().isOk()).andExpect(content().json(myTasks));

	}
	
	@Test
	@WithMockUser(username = ADMIN_LOGIN, password = ADMIN_LOGIN, roles = { ADMIN_ROLE })
	public void getStatementAdminSuccess() throws Exception {
		
		when(accountsService.getStatementLastMonths(Mockito.anyInt())).thenReturn(Arrays.asList(mockStatement));
		
		String myTasks = "[{\"id\":1,\"accountId\":3,\"date\":\"16.07.2020\",\"amount\":\"320.113318991709\"}]";

        mockMvc.perform(get(String.format("/accounts/%s/statements", 3))).andExpect(status().isOk()).andExpect(content().json(myTasks));

	}
	
	@Test
	@WithMockUser(username = USER_LOGIN, password = USER_LOGIN, roles = { USER_ROLE })
	public void getStatementByDateRangeAccountNotFound() throws Exception {
		
		when(accountsService.getStatementLastMonths(Mockito.anyInt())).thenReturn(null);
		
		String myTasks = "No result found";

        mockMvc.perform(get(String.format("/accounts/%s/statements", 3))).andExpect(status().isNotFound()).andExpect(content().string(myTasks));

	}
	
	
	@Test
	@WithMockUser(username = ADMIN_LOGIN, password = ADMIN_LOGIN, roles = { ADMIN_ROLE })
	public void getStatementByDateRangeSuccess() throws Exception {
		
		when(accountsService.getStatementByDateRange(Mockito.anyInt(), Mockito.any(), Mockito.any())).thenReturn(Arrays.asList(mockStatement));
		
		String myTasks = "[{\"id\":1,\"accountId\":3,\"date\":\"16.07.2020\",\"amount\":\"320.113318991709\"}]";

        mockMvc.perform(get(String.format("/accounts/%s/statements?dateStart=09.08.2020&&dateEnd=19.08.2021", 3))).andExpect(status().isOk()).andExpect(content().json(myTasks));

	}
	
	//test with the same date
	@Test
	@WithMockUser(username = ADMIN_LOGIN, password = ADMIN_LOGIN, roles = { ADMIN_ROLE })
	public void getStatementByDateRangeEqual() throws Exception {
		
		when(accountsService.getStatementByDateRange(Mockito.anyInt(), Mockito.any(), Mockito.any())).thenReturn(Arrays.asList(mockStatement));
		
		String myTasks = "[{\"id\":1,\"accountId\":3,\"date\":\"16.07.2020\",\"amount\":\"320.113318991709\"}]";

		String dateStr = "16.07.2020";
        mockMvc.perform(get(String.format("/accounts/%s/statements?dateStart=%s&&dateEnd=%s", 3, dateStr, dateStr))).andExpect(status().isOk()).andExpect(content().json(myTasks));

	}
	
	//test with the same date
	@Test
	@WithMockUser(username = ADMIN_LOGIN, password = ADMIN_LOGIN, roles = { ADMIN_ROLE })
	public void getStatementByAmountRangeEqual() throws Exception {

		when(accountsService.getStatementByAmountRange(Mockito.anyInt(), Mockito.any(), Mockito.any()))
				.thenReturn(Arrays.asList(mockStatement));

		String myTasks = "[{\"id\":1,\"accountId\":3,\"date\":\"16.07.2020\",\"amount\":\"320.113318991709\"}]";

		String amount = "320";
		mockMvc.perform(get(String.format("/accounts/%s/statements?amountStart=%s&&amountEnd=%s", 3, amount, amount)))
				.andExpect(status().isOk()).andExpect(content().json(myTasks));

	}
	
	@Test
	@WithMockUser(username = ADMIN_LOGIN, password = ADMIN_LOGIN, roles = { ADMIN_ROLE })
	public void getStatementByAmountRangeStartFormat() throws Exception {
		
		when(accountsService.getStatementByAmountRange(Mockito.anyInt(), Mockito.any(), Mockito.any())).thenReturn(Arrays.asList(mockStatement));
		
		String myTasks = "Invalid argument type : For input string: \"XX\"";

        mockMvc.perform(get(String.format("/accounts/%s/statements?amountStart=XX&&amountEnd=20", 3))).andExpect(status().isBadRequest()).andExpect(content().string(myTasks));

	}
	
	@Test
	@WithMockUser(username = ADMIN_LOGIN, password = ADMIN_LOGIN, roles = { ADMIN_ROLE })
	public void getStatementByAmountRangeEndFormat() throws Exception {
		
		when(accountsService.getStatementByAmountRange(Mockito.anyInt(), Mockito.any(), Mockito.any())).thenReturn(Arrays.asList(mockStatement));
		
		String myTasks = "Invalid argument type : For input string: \"20.2\"";

        mockMvc.perform(get(String.format("/accounts/%s/statements?amountStart=10&&amountEnd=20.2", 3))).andExpect(status().isBadRequest()).andExpect(content().string(myTasks));

	}
	
	
	@Test
	@WithMockUser(username = ADMIN_LOGIN, password = ADMIN_LOGIN, roles = { ADMIN_ROLE })
	public void getStatementByDateRangeStartFormat() throws Exception {
		
		when(accountsService.getStatementByDateRange(Mockito.anyInt(), Mockito.any(), Mockito.any())).thenReturn(Arrays.asList(mockStatement));
		
		String myTasks = "Text '2021.07.10' could not be parsed at index 2";

        mockMvc.perform(get(String.format("/accounts/%s/statements?dateStart=2021.07.10&&dateEnd=16.07.2020", 3))).andExpect(status().isBadRequest()).andExpect(content().string(myTasks));

	}
	
	@Test
	@WithMockUser(username = ADMIN_LOGIN, password = ADMIN_LOGIN, roles = { ADMIN_ROLE })
	public void getStatementByDateRangeEndFormat() throws Exception {
		
		when(accountsService.getStatementByDateRange(Mockito.anyInt(), Mockito.any(), Mockito.any())).thenReturn(Arrays.asList(mockStatement));
		
		String myTasks = "Text '16/07/2021' could not be parsed at index 2";

        mockMvc.perform(get(String.format("/accounts/%s/statements?dateStart=16.07.2020&&dateEnd=16/07/2021", 3))).andExpect(status().isBadRequest()).andExpect(content().string(myTasks));

	}
	
	/**
	 * test the case where range1 > range2
	 * @throws Exception
	 */
	@Test
	@WithMockUser(username = ADMIN_LOGIN, password = ADMIN_LOGIN, roles = { ADMIN_ROLE })
	public void getStatementByDateRangeIncompatible() throws Exception {
		
		when(accountsService.getStatementByDateRange(Mockito.anyInt(), Mockito.any(), Mockito.any())).thenReturn(Arrays.asList(mockStatement));
		
		String myTasks = "dateStart should be inferior or equal to dateEnd";

		
        mockMvc.perform(get(String.format("/accounts/%s/statements?dateStart=16.07.2021&&dateEnd=15.07.2021", 3))).andExpect(status().isBadRequest()).andExpect(content().string(myTasks));

	}
	
	/**
	 * test the case where range1 > range2
	 * @throws Exception
	 */
	@Test
	@WithMockUser(username = ADMIN_LOGIN, password = ADMIN_LOGIN, roles = { ADMIN_ROLE })
	public void getStatementByAmountRangeIncompatible() throws Exception {
		
		when(accountsService.getStatementByAmountRange(Mockito.anyInt(), Mockito.any(), Mockito.any())).thenReturn(Arrays.asList(mockStatement));
		
		String myTasks = "amountStart should be inferior or equal to amountEnd";

		
        mockMvc.perform(get(String.format("/accounts/%s/statements?amountStart=200&&amountEnd=100", 3))).andExpect(status().isBadRequest()).andExpect(content().string(myTasks));

	}
	
	/**
	 * test the case where range1 > range2
	 * @throws Exception
	 */
	@Test
	@WithMockUser(username = ADMIN_LOGIN, password = ADMIN_LOGIN, roles = { ADMIN_ROLE })
	public void getStatementByMultiRange() throws Exception {
		
		when(accountsService.getStatementByAmountRange(Mockito.anyInt(), Mockito.any(), Mockito.any())).thenReturn(Arrays.asList(mockStatement));
		
		String myTasks = "Multirange not allowed";

		
        mockMvc.perform(get(String.format("/accounts/%s/statements?amountStart=200&&amountEnd=100&&dateStart=16.07.2021&&dateEnd=20.07.2021", 3))).andExpect(status().isBadRequest()).andExpect(content().string(myTasks));

	}
	
	/**
	 * test the case where dateStart but not dateEnd
	 * @throws Exception
	 */
	@Test
	@WithMockUser(username = ADMIN_LOGIN, password = ADMIN_LOGIN, roles = { ADMIN_ROLE })
	public void getStatementByDateRangeMissing() throws Exception {
		
		when(accountsService.getStatementByDateRange(Mockito.anyInt(), Mockito.any(), Mockito.any())).thenReturn(Arrays.asList(mockStatement));
		
		String myTasks = "Date range invalid";

		
        mockMvc.perform(get(String.format("/accounts/%s/statements?dateStart=16.07.2021", 3))).andExpect(status().isBadRequest()).andExpect(content().string(myTasks));

	}
	
	/**
	 * test the case where dateStart but not dateEnd
	 * @throws Exception
	 */
	@Test
	@WithMockUser(username = ADMIN_LOGIN, password = ADMIN_LOGIN, roles = { ADMIN_ROLE })
	public void getStatementByAmountRangeMissing() throws Exception {
		
		when(accountsService.getStatementByDateRange(Mockito.anyInt(), Mockito.any(), Mockito.any())).thenReturn(Arrays.asList(mockStatement));
		
		String myTasks = "Amount range invalid";

		
        mockMvc.perform(get(String.format("/accounts/%s/statements?amountStart=100", 3))).andExpect(status().isBadRequest()).andExpect(content().string(myTasks));

	}
	
	@Test
	@WithMockUser(username = USER_LOGIN, password = USER_LOGIN, roles = { USER_ROLE })
	public void getStatementAccountIDMissing() throws Exception {
		
		
		when(accountsService.getStatementLastMonths(Mockito.anyInt())).thenReturn(Arrays.asList(mockStatement));
		
		String myTasks = "Missing path variable";

        mockMvc.perform(get("/accounts/statements/")).andExpect(status().isBadRequest()).andExpect(content().string(myTasks));
        
        // /statements/%s?dateStart=09.08.2020&&dateEnd=19.08.2021

	}
	
	
}