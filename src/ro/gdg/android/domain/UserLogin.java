package ro.gdg.android.domain;

public class UserLogin {

	private String accountEmail;
	private String password;

	public UserLogin(String accountEmail, String password) {
		super();
		this.accountEmail = accountEmail;
		this.password = password;
	}

	public String getAccountEmail() {
		return accountEmail;
	}

	public String getPassword() {
		return password;
	}

	@Override
	public String toString() {
		return "UserLogin [accountEmail=" + accountEmail + ", password="
				+ password + "]";
	}

}
