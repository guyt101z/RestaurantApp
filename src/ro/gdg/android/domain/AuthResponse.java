package ro.gdg.android.domain;


public class AuthResponse extends RestaurantResponse {

	public static final String AUTH_STATUS_OK = "OK";

	private String authStatus;
	private AccountInfo account;

	public String getAuthStatus() {
		return authStatus;
	}

	public void setAuthStatus(String authStatus) {
		this.authStatus = authStatus;
	}

	public AccountInfo getAccount() {
		return account;
	}

	public void setAccount(AccountInfo account) {
		this.account = account;
	}

	@Override
	public String toString() {
		return "AuthResponse [authStatus=" + authStatus + ", account="
				+ account + "]";
	}

}
