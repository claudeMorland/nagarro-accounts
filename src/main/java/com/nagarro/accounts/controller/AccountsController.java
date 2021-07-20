package com.nagarro.accounts.controller;

import static com.nagarro.accounts.common.ApplicationConstants.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Positive;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nagarro.accounts.error.functional.AccountBadRequestException;
import com.nagarro.accounts.error.functional.ResourceNotFoundException;
import com.nagarro.accounts.persistence.entity.Statement;
import com.nagarro.accounts.services.AccountsService;

/**
 * Accounts Controller manage accounts and statement
 * @author claud
 *
 */

@RequestMapping("/accounts")
@RestController
@Validated
public class AccountsController {

	private static final Logger LOGGER = LoggerFactory.getLogger(AccountsController.class);

	@Autowired
	private AccountsService accountsService;

	@GetMapping("/ping")
	public String ping() {
		return "Application acccounts is running";

	}

	@GetMapping(value= {"/statements", "/{accountId}/statements"})
	public ResponseEntity<Object> getStatements(
			@Valid @Positive(message = "{accountId.positive}")@PathVariable Integer accountId,//
			@Valid @RequestParam(required = false) @DateTimeFormat(pattern = DATE_FORMAT_API) LocalDate dateStart,// 
			@Valid @RequestParam(required = false) @DateTimeFormat(pattern = DATE_FORMAT_API) LocalDate dateEnd,//
			@Valid @Positive(message = "{amountStart.positive}") @RequestParam(required = false) Integer amountStart, //
			@Valid @Positive(message = "{amountEnd.positive}") @RequestParam(required = false) Integer amountEnd) {
		LOGGER.debug("getStatementByDateRange");

		List<Statement> statements = new ArrayList<>();
		
		this.checkRequestParam(dateStart, dateEnd, amountStart, amountEnd);
		
		//in checkRequestParam we already check that both start and end
		// of a range come together
		
		//if one range is present 
		if (dateStart == null && amountStart==null) {
			statements = this.accountsService.getStatementLastMonths(accountId);
		}

		if (dateStart != null) {
			statements = this.accountsService.getStatementByDateRange(accountId, dateStart, dateEnd);
		}

		if (amountStart != null) {
			statements = this.accountsService.getStatementByAmountRange(accountId, amountStart, amountEnd);
		}
		
		if (statements.isEmpty()) {
			throw new ResourceNotFoundException("No result found");
		}

		return ResponseEntity.ok().body(statements);
	}

	/**
	 * Check the request param : check that the range are valid with start and end
	 * and the range1 <= range2
	 * @param dateStart
	 * @param dateEnd
	 * @param amountStart
	 * @param amountEnd
	 * @throws AccountBadRequestException if reach functional error on param
	 */
	private void checkRequestParam(LocalDate dateStart, LocalDate dateEnd, Integer amountStart,
			Integer amountEnd) throws AccountBadRequestException {
		
		if ( (amountStart != null && amountEnd == null) || (amountStart == null && amountEnd != null)) {
			throw new AccountBadRequestException("Amount range invalid");
		}
		
		if ( (dateStart != null && dateEnd == null) || (dateStart == null && dateEnd != null)) {
			throw new AccountBadRequestException("Date range invalid");
		}

		//we check only the start as range compatibility is check before
		if (dateStart != null && amountStart !=null) {
			throw new AccountBadRequestException("Multirange not allowed");
		}						
		
		//date range1 <= date rang2 (equal is good)
		if (dateStart != null && dateStart.isAfter(dateEnd)) {
			throw new AccountBadRequestException("dateStart should be inferior or equal to dateEnd");	
		}		
		
		//amount range1 <= amount rang2 (equal is good)
		if (amountStart != null && amountStart > amountEnd) {
			throw new AccountBadRequestException("amountStart should be inferior or equal to amountEnd");
		}
	}

}
