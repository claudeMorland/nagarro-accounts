package com.nagarro.accounts.persistence.entity;

import java.util.Objects;

/**
 * Entity for Account
 * @author claud
 *
 */

public class Account {
	
	//type of account
	private String type;
	//account number
	private String number;
	
	

	/**
	 * @param type
	 * @param number
	 */
	public Account(String type, String number) {
		super();
		this.type = type;
		this.number = number;
	}


	public Account() {
	}


	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the number
	 */
	public String getNumber() {
		return number;
	}

	/**
	 * @param number the number to set
	 */
	public void setNumber(String number) {
		this.number = number;
	}

	@Override
	public int hashCode() {
		return Objects.hash(number, type);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Account other = (Account) obj;
		return Objects.equals(number, other.number) && Objects.equals(type, other.type);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Account [type=").append(type).append(", number=").append(number)
				.append("]");
		return builder.toString();
	}
	

}
