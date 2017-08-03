package org.openmrs.module.m2sysbiometrics.util;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class Token implements Serializable {
	
	private static final long serialVersionUID = 2642478741110327810L;
	
	@JsonProperty("access_token")
	private String accessToken;
	
	@JsonProperty("token_type")
	private String tokenType;
	
	@JsonProperty("expires_in")
	private String expiresIn;
	
	public String getAccessToken() {
		return accessToken;
	}
	
	public String getExpiresIn() {
		return expiresIn;
	}
	
	public String getTokenType() {
		return tokenType;
	}
	
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	
	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}
	
	public void setExpiresIn(String expiresIn) {
		this.expiresIn = expiresIn;
	}
}
