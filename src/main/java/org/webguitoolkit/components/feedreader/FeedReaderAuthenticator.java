package org.webguitoolkit.components.feedreader;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

public class FeedReaderAuthenticator extends Authenticator {
	private String username, password;

	public FeedReaderAuthenticator(String username, String password) {
		this.username = username;
		this.password = password;
	}

	/**
	 * This method is called when a password-protected URL is accessed
	 */
	protected PasswordAuthentication getPasswordAuthentication() {
		String username = this.username;
		String password = this.password;

		// Return the information
		return new PasswordAuthentication(username, password.toCharArray());
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}
}
