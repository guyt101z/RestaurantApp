package ro.gdg.android.api;

import ro.gdg.android.Settings;
import ro.gdg.android.db.TableBillsHistory;
import ro.gdg.android.domain.UserLogin;
import ro.gdg.android.net.CheckCredentialsTask;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class RestaurantServiceClient {

	private static final String TAG = RestaurantServiceClient.class.getSimpleName();

	private static final String LAST_REPORTS_RECEIVED_TIME = "last_reports_received_time";
	public static final long REQUEST_TIME_INTERVAL = 120000L;

	private TableBillsHistory tableBillsHistory;
	private RestServiceClient restServiceClient;
	private long lastReportsReceivedTime;
	private UserLogin userLogin;
	private Settings settings;
	private Context context;

	volatile CheckCredentialsTask checkCredentialsTask;

	public RestaurantServiceClient(Context context) {
		this.context = context;
		this.lastReportsReceivedTime = PreferenceManager
				.getDefaultSharedPreferences(context).getLong(
						LAST_REPORTS_RECEIVED_TIME, 0);

		this.restServiceClient = new RestServiceClient();
		tableBillsHistory = new TableBillsHistory(context);
		settings = new Settings(context);

		changeUser(Settings.getUserLogin(context));
	}

	public RestServiceClient getRestServiceClient() {
		return restServiceClient;
	}

	public TableBillsHistory getTableBillsHistory() {
		return tableBillsHistory;
	}

	public UserLogin getUserLogin() {
		return userLogin;
	}

	public Settings getSettings() {
		return settings;
	}

	public boolean isLoggedIn() {
		return (userLogin != null);
	}

	public void setUserLogin(UserLogin userLogin) {
		Log.d(TAG, "setUserLogin userLogin = " + userLogin);
		changeUser(userLogin);

		if (userLogin != null) {
			settings.setUserLogin(userLogin);
		} else {
			settings.setUserLogin(null);
		}
	}

	public void changeUser(UserLogin userLogin) {
		this.userLogin = userLogin;
		restServiceClient.setUserCredentials(userLogin);

		if (userLogin != null) {
			if (!userLogin.getAccountEmail()
					.equalsIgnoreCase(settings.getEmail())) {
				// syncReportsRun();
			}
		} else {
			// reset lastReportsReceivedTime when log out in order to sync
			// reports when logging
			// in with another user
			lastReportsReceivedTime = 0;
			SharedPreferences.Editor editor = PreferenceManager
					.getDefaultSharedPreferences(context).edit();
			editor.putLong(LAST_REPORTS_RECEIVED_TIME, lastReportsReceivedTime);
			editor.commit();
		}
	}
}
