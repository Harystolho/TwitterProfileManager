package com.harystolho.twitter;

public class TwitterConnector {

	private String username;
	private String password;

	public static class Builder {

		private String username;
		private String password;

		public Builder() {

		}

		public Builder setUsername(String username) {
			this.username = username;
			return this;
		}

		public Builder setPassword(String password) {
			this.password = password;
			return this;
		}

		public TwitterConnector build() {

			TwitterConnector tc = new TwitterConnector();

			tc.setUsername(username);
			tc.setPassword(password);

			return tc;
		}

	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
