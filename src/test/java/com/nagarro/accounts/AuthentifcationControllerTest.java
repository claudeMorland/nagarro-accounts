package com.nagarro.accounts;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.nagarro.accounts.security.config.SecurityConfiguration;
import com.nagarro.accounts.security.controller.AuthentificationController;
import com.nagarro.accounts.security.model.JwtRequest;
import com.nagarro.accounts.security.model.JwtResponse;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@WebMvcTest(value = AuthentificationController.class)
@Import(SecurityConfiguration.class)
public class AuthentifcationControllerTest {
	
	@Configuration
	@ComponentScan("com.nagarro.accounts")
    public static class TestConfiguration {
    }
	
    @InjectMocks
    private AuthentificationController authentificationController;

  
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private JdbcTemplate jdbcTemplate;
	
	
	private final String userJsonBody = "{\"username\": \"user\", \"password\": \"user\"}";
	
	private final String adminJsonBody = "{\"username\": \"admin\", \"password\": \"admin\"}";
	
	private final String unknownJsonBody = "{\"username\": \"xxx\", \"password\": \"xxx\"}";
	
	@Test
	public void authenticateUser() throws Exception {
		
		 mockMvc
         .perform(post("/authenticate")
           .content(userJsonBody)
           .contentType("application/json"))
         .andDo(print())
         .andExpect(status().isOk());

	}
	
	@Test
	public void authenticateAdmin() throws Exception {
		
		 mockMvc
         .perform(post("/authenticate")
           .content(adminJsonBody)
           .contentType("application/json"))
         .andDo(print())
         .andExpect(status().isOk());

	}
	
	@Test
	public void authenticateUnknown() throws Exception {
		
		 mockMvc
         .perform(post("/authenticate")
           .content(unknownJsonBody)
           .contentType("application/json"))
         .andDo(print())
         .andExpect(status().isNotFound());

	}
	
	@Test
	public void authenticateUserMissing() throws Exception {
		final String userMissingJsonBody = "{\"password\": \"pwd\"}";
		 mockMvc
         .perform(post("/authenticate")
           .content(userMissingJsonBody)
           .contentType("application/json"))
         .andDo(print())
         .andExpect(status().isBadRequest());

	}
	
	
	@Test
	public void compareJwtRequest() throws Exception {
		JwtRequest jwtRequest1 = new JwtRequest();
		jwtRequest1.setUsername("username");
		jwtRequest1.setPassword("password");
		
		JwtRequest jwtRequest2 = new JwtRequest();
		jwtRequest2.setUsername(jwtRequest1.getUsername());
		jwtRequest2.setPassword(jwtRequest1.getPassword());
		
		
		assertEquals(jwtRequest1, jwtRequest2);		
		assertEquals(jwtRequest1.toString(), jwtRequest2.toString());
		
	}
	
	@Test
	public void compareJwtResponse() throws Exception {
		JwtResponse jwtResponse1 = new JwtResponse();
		jwtResponse1.setJwtToken("jwtToken");
		
		JwtResponse jwtResponse2 = new JwtResponse();
		jwtResponse2.setJwtToken(jwtResponse1.getJwtToken());
		
		assertEquals(jwtResponse1, jwtResponse2);		
		assertEquals(jwtResponse1.toString(), jwtResponse2.toString());
		
	}
	

	
}