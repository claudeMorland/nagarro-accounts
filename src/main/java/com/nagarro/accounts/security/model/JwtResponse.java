package com.nagarro.accounts.security.model;
import java.util.Objects;


/**
 * Response for Authentification api
 * @author claud
 *
 */
public class JwtResponse {

    private String jwtToken;

	/**
	 * @param jwtToken
	 */
	public JwtResponse(String jwtToken) {
		super();
		this.jwtToken = jwtToken;
	}

	/**
	 * @return the jwtToken
	 */
	public String getJwtToken() {
		return jwtToken;
	}

	/**
	 * @param jwtToken the jwtToken to set
	 */
	public void setJwtToken(String jwtToken) {
		this.jwtToken = jwtToken;
	}

	@Override
	public int hashCode() {
		return Objects.hash(jwtToken);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		JwtResponse other = (JwtResponse) obj;
		return Objects.equals(jwtToken, other.jwtToken);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("JwtResponse [jwtToken=").append(jwtToken).append("]");
		return builder.toString();
	}

	
    
    
}