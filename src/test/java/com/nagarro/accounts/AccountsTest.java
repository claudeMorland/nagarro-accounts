package com.nagarro.accounts;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.nagarro.accounts.controller.AccountsController;
import com.nagarro.accounts.persistence.entity.Account;
import com.nagarro.accounts.persistence.entity.Statement;

@SpringBootTest
public class AccountsTest {

	@Autowired
	private AccountsController controller;

	@Test
	public void contextLoads() throws Exception {
		assertThat(controller).isNotNull();
	}
	
	@Test
	public void compareStatement() throws Exception {
		Statement statement1 = new Statement();
		statement1.setId(1);
		statement1.setAccountId(11);
		statement1.setDate("2021-07-19");
		statement1.setAmount("30.00");
				
		Account account1 = new Account();
		account1.setNumber("1111");
		account1.setType("current");
		statement1.setAccount(account1);
		
		Statement statement2 = new Statement();
		statement2.setId(statement1.getId());
		statement2.setAccountId(statement1.getAccountId());
		statement2.setDate(statement1.getDate());
		statement2.setAmount(statement1.getAmount());
		
		Account account2 = new Account();
		account2.setNumber(account1.getNumber());
		account2.setType(account1.getType());
		statement2.setAccount(account2);
		
		assertEquals(statement1, statement2);
		assertEquals(account1, account2);		
		assertEquals(statement1.getAccount(), statement2.getAccount());
		
		assertEquals(statement1.toString(), statement2.toString());
		assertEquals(account1.toString(), account2.toString());
				
		account2.setType("periodic");
		assertNotEquals(statement1, statement2);
		assertNotEquals(account1, account2);
		assertNotEquals(statement1.getAccount(), statement2.getAccount());
		
		
		assertNotEquals(statement1, null);		
		assertNotEquals(account1, null);
		assertNotEquals(statement1.getAccount(), null);
	}
}