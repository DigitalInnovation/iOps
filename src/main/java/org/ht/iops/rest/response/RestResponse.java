package org.ht.iops.rest.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class RestResponse {
	@JsonIgnore
	public String responseCode;
	@JsonIgnore
	public List<String> tokens;
	/**
	 * @return the responseCode
	 */
	public String getResponseCode() {
		return responseCode;
	}
	/**
	 * @param responseCode
	 *            the responseCode to set
	 */
	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}
	/**
	 * @return the tokens
	 */
	public List<String> getTokens() {
		return tokens;
	}
	/**
	 * @param tokens
	 *            the tokens to set
	 */
	public void setTokens(List<String> tokens) {
		this.tokens = tokens;
	}
}
