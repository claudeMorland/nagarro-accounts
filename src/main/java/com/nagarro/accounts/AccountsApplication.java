package com.nagarro.accounts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Main Spring boot application
 * @author claud
 *
 */
@SpringBootApplication
@ComponentScan("com.nagarro.accounts")
public class AccountsApplication {

	public static void main(String[] args) {
		SpringApplication.run(AccountsApplication.class, args);	
		
	}

}
