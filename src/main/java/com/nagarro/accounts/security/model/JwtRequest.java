package com.nagarro.accounts.security.model;

import java.util.Objects;

import javax.validation.constraints.NotBlank;

/**
 * Request param for authentification api
 * @author claud
 *
 */
public class JwtRequest {

	@NotBlank(message = "{auth.username.empty}")
    private String username;
	
	@NotBlank(message = "{auth.password.empty}")
    private String password;
	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}
	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	@Override
	public int hashCode() {
		return Objects.hash(password, username);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		JwtRequest other = (JwtRequest) obj;
		return Objects.equals(password, other.password) && Objects.equals(username, other.username);
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("JwtRequest [username=").append(username).append(", password=").append(password).append("]");
		return builder.toString();
	}
	
	
	
	
    
}