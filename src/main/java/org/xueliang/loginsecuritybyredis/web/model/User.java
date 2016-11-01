package org.xueliang.loginsecuritybyredis.web.model;

public class User {

	private String username;
	private String nickname;
	
	public User(String username, String nickname) {
		this.username = username;
		this.nickname = nickname;
	}
	
	@Override
	public String toString() {
		return String.format("User[username=%s,nickname=%s]", username, nickname);
	}
}
