package ro.gdg.android.domain;

public class AccountInfo {

	private String email;

	public AccountInfo(String email) {
		this.email = email;
	}

	public String getEmail() {
		return email;
	}

	@Override
	public String toString() {
		return "AccountInfo [email=" + email + "]";
	}

}
