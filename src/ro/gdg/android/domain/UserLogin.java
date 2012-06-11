package ro.gdg.android.domain;


public class UserLogin {

	private AccountInfo accountInfo;
	private String password;

	public UserLogin(AccountInfo accountInfo, String password) {
		super();
		this.accountInfo = accountInfo;
		this.password = password;
	}

	public UserLogin(String user, String password) {
		accountInfo = new AccountInfo(user);
		this.password = password;
	}

	public AccountInfo getAccountInfo() {
		return accountInfo;
	}

	public String getPassword() {
		return password;
	}

	@Override
	public String toString() {
		return "UserLogin [accountInfo=" + accountInfo + ", password="
				+ password + "]";
	}

}
