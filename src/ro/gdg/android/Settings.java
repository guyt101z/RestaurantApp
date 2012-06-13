package ro.gdg.android;

import ro.gdg.android.domain.UserLogin;
import android.content.Context;
import android.content.SharedPreferences;

public class Settings {

	public static final String KEY_EMAIL = "email";

	public static final String KEY_USER_PASSWORD = "user_password";

	private static final String NAME = "restaurant";

	SharedPreferences sharedPreferences;

	public Settings(Context context) {
		sharedPreferences = context.getSharedPreferences(NAME,
				Context.MODE_PRIVATE);
	}

	public String getEmail() {
		return sharedPreferences.getString(KEY_EMAIL, "");
	}

	static public UserLogin getUserLogin(Context context) {
		SharedPreferences sp = context.getSharedPreferences(NAME,
				Context.MODE_PRIVATE);

		String name = sp.getString(KEY_EMAIL, null);
		String password = sp.getString(KEY_USER_PASSWORD, null);
		if (name != null && password != null) {
			return new UserLogin(name, password);
		} else {
			return null;
		}
	}

	public void setUserLogin(UserLogin userLogin) {
		SharedPreferences.Editor spe = sharedPreferences.edit();
		if (userLogin != null) {
			spe.putString(KEY_EMAIL, userLogin.getAccountEmail());
			spe.putString(KEY_USER_PASSWORD, userLogin.getPassword());
		} else {
			spe.remove(KEY_USER_PASSWORD);
		}
		spe.commit();
	}
}
