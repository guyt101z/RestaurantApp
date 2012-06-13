package ro.gdg.android.domain;


public class AuthResponse extends RestaurantResponse {

	public static final String AUTH_STATUS_OK = "OK";

	private String authStatus;
	private String accountEmail;

	public String getAuthStatus() {
		return authStatus;
	}

	public void setAuthStatus(String authStatus) {
		this.authStatus = authStatus;
	}

	public String getAccount() {
		return accountEmail;
	}

	public void setAccount(String accountEmail) {
		this.accountEmail = accountEmail;
	}

	@Override
	public String toString() {
		return "AuthResponse [authStatus=" + authStatus + ", accountEmail="
				+ accountEmail + "]";
	}

}
