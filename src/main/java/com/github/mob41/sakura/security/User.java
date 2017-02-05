package com.github.mob41.sakura.security;

import com.github.mob41.sakura.hash.AES_SHA512;

/**
 * A <code>User</code> class representing a user in LoginAgentPlugin
 * @author Anthony
 *
 */
public class User {

	private final String username;
	
	private final String passhash;
	
	private final String salt;
	
	public User(String username, String passhash, String salt){
		this.username = username;
		this.passhash = passhash;
		this.salt = salt;
	}
	
	public User(String username, String password) throws Exception{
		this.username = username;
		
		//Encrypt password
		salt = AES_SHA512.getRandomSalt();
		passhash = AES_SHA512.encrypt(password, salt);
	}
	
	protected String getSalt(){
		return salt;
	}
	
	public String getUsername(){
		return username;
	}
	
	protected String getPassHash(){
		return passhash;
	}
	
	public boolean equals(User anotherUser){
		return this.username.equals(anotherUser.getUsername()) && 
				this.passhash.equals(anotherUser.getPassHash());
	}
}
