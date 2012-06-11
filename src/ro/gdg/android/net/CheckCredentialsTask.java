package ro.gdg.android.net;

import ro.gdg.android.api.RestaurantServiceClient;
import ro.gdg.android.domain.AuthResponse;
import ro.gdg.android.domain.UserLogin;
import android.os.AsyncTask;
import android.util.Log;

public class CheckCredentialsTask extends
		AsyncTask<UserLogin, Void, AuthResponse> {
	private static final String TAG = CheckCredentialsTask.class
			.getSimpleName();

	volatile CheckCredentialsListener listener;

	private RestaurantServiceClient serviceClient;
	private UserLogin userLogin;

	public CheckCredentialsTask(RestaurantServiceClient serviceClient) {
		this.serviceClient = serviceClient;
	}

	public void setListener(CheckCredentialsListener listener) {
		this.listener = listener;
	}

	@Override
	protected AuthResponse doInBackground(UserLogin... logins) {
		userLogin = logins[0];
		serviceClient.getRestServiceClient().setUserCredentials(userLogin);

		// delete history if new user logs in
		if (!userLogin.getAccountInfo().getEmail()
				.equalsIgnoreCase(serviceClient.getSettings().getEmail())) {
			Log.d(TAG, "new user => delete history");
			// serviceClient.getReportsHistory().deleteAllRecords();
		}

		return serviceClient.getRestServiceClient().checkCredentials(
				userLogin.getAccountInfo().getEmail(), userLogin.getPassword());
	}

	@Override
	protected void onPostExecute(AuthResponse response) {
		if (response != null && response.getAccount() != null) {
			serviceClient.setUserLogin(new UserLogin(response.getAccount(),
					userLogin.getPassword()));
		}
		if (listener != null) {
			listener.done(response);
		}
	}
}