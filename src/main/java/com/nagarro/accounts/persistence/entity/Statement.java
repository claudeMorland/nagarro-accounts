package com.nagarro.accounts.persistence.entity;

import java.util.Objects;

import javax.validation.constraints.NotNull;

/**
 * Entity for Statement
 * Contains embedded field account
 * @author claud
 *
 */
public class Statement {
	
	@NotNull
	private Integer id;
	
	@NotNull
	private Integer accountId;
	
	@NotNull
	private String date;
	
	@NotNull
	private String amount;
	
	
	private Account account;
	
	
	
	/**
	 * Constructor with account info
	 * @param id
	 * @param accountId
	 * @param date
	 * @param amount
	 * @param accountType
	 * @param accountNumber
	 */
	public Statement(Integer id, Integer accountId, String date, String amount, String accountType, String accountNumber) {
		super();
		this.id = id;
		this.accountId = accountId;
		this.date = date;
		this.amount = amount;
		
		this.account = new Account(accountType, accountNumber);
	}
	

	public Statement() {
	}


	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the accountId
	 */
	public Integer getAccountId() {
		return accountId;
	}

	/**
	 * @param accountId the accountId to set
	 */
	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}

	/**
	 * @return the date
	 */
	public String getDate() {
		return date;
	}

	/**
	 * @param date the date to set
	 */
	public void setDate(String date) {
		this.date = date;
	}

	/**
	 * @return the amount
	 */
	public String getAmount() {
		return amount;
	}

	/**
	 * @param amount the amount to set
	 */
	public void setAmount(String amount) {
		this.amount = amount;
	}
	
	

	/**
	 * @return the account
	 */
	public Account getAccount() {
		return account;
	}

	/**
	 * @param account the account to set
	 */
	public void setAccount(Account account) {
		this.account = account;
	}


	@Override
	public int hashCode() {
		return Objects.hash(account, accountId, amount, date, id);
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Statement other = (Statement) obj;
		return Objects.equals(account, other.account) && Objects.equals(accountId, other.accountId)
				&& Objects.equals(amount, other.amount) && Objects.equals(date, other.date)
				&& Objects.equals(id, other.id);
	}


	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Statement [id=").append(id).append(", accountId=").append(accountId).append(", date=")
				.append(date).append(", amount=").append(amount).append(", account=").append(account).append("]");
		return builder.toString();
	}

	
	
	

}
