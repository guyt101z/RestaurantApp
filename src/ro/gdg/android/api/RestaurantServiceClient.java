package ro.gdg.android.api;

import ro.gdg.android.Settings;
import ro.gdg.android.db.TableBillsHistory;
import ro.gdg.android.domain.TBill;
import ro.gdg.android.domain.TableBillsResponse;
import ro.gdg.android.domain.UserLogin;
import ro.gdg.android.net.CheckCredentialsTask;
import ro.gdg.android.net.SyncHistoryTask;
import ro.gdg.android.net.TableBillsHistoryUpdateListener;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class RestaurantServiceClient {

	private static final String TAG = RestaurantServiceClient.class
			.getSimpleName();

	private static final String LAST_BILLS_RECEIVED_TIME = "last_bills_received_time";
	public static final long REQUEST_TIME_INTERVAL = 120000L;

	private TableBillsHistory tableBillsHistory;
	private RestServiceClient restServiceClient;
	private long lastBillsReceivedTime;
	private UserLogin userLogin;
	private Settings settings;
	private Context context;

	volatile SyncHistoryTask tableBillsTask;
	volatile TableBillsHistoryUpdateListener historyListener;

	volatile CheckCredentialsTask checkCredentialsTask;

	public RestaurantServiceClient(Context context) {
		this.context = context;
		this.lastBillsReceivedTime = PreferenceManager
				.getDefaultSharedPreferences(context).getLong(
						LAST_BILLS_RECEIVED_TIME, 0);

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

	public int getTableState(int tableNo) {
		return tableBillsHistory.getStateOfTable(userLogin.getAccountEmail(),
				tableNo);
	}

	public boolean hasOpenBill(int tableNo) {
		return tableBillsHistory.hasOpenTableBill(tableNo);
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
			if (!userLogin.getAccountEmail().equalsIgnoreCase(
					settings.getEmail())) {
				// syncReportsRun();
			}
		} else {
			// reset lastReportsReceivedTime when log out in order to sync
			// reports when logging
			// in with another user
			lastBillsReceivedTime = 0;
			SharedPreferences.Editor editor = PreferenceManager
					.getDefaultSharedPreferences(context).edit();
			editor.putLong(LAST_BILLS_RECEIVED_TIME, lastBillsReceivedTime);
			editor.commit();
		}
	}

	public void setTableBillsListener(TableBillsHistoryUpdateListener listener) {
		this.historyListener = listener;
		if (listener != null && isTableBillsSyncInProgress()) {
			listener.onSyncStarted();
		}
	}

	private void startTableBillsTask() {
		tableBillsTask = new SyncHistoryTask(this);
		tableBillsTask.execute();
		if (historyListener != null) {
			historyListener.onSyncStarted();
		}
	}

	public void done(TableBillsResponse response) {
		tableBillsTask = null;
		if (historyListener != null) {
			historyListener.onSyncFinished(response);
		}
	}

	public void markBillsReceived() {
		Log.d(TAG, "mark bills received");
		lastBillsReceivedTime = System.currentTimeMillis();
		SharedPreferences.Editor editor = PreferenceManager
				.getDefaultSharedPreferences(context).edit();
		editor.putLong(LAST_BILLS_RECEIVED_TIME, lastBillsReceivedTime);
		editor.commit();
	}

	public boolean shouldRequestTableBills() {
		if (lastBillsReceivedTime == 0) {
			Log.d(TAG, "sync history wasn't made until now..sync now");
			return true;
		}

		long time = System.currentTimeMillis();
		Log.d(TAG, "currentTime: " + time);
		if (time - lastBillsReceivedTime > REQUEST_TIME_INTERVAL) {
			return true;
		}
		return false;
	}

	public void syncTableBills() {
		// if the task is not already started
		if (tableBillsTask == null && shouldRequestTableBills()) {
			Log.d(TAG, "should sync bills ... starting task");
			startTableBillsTask();
		} else {
			Log.d(TAG, "should not sync bills");
		}
	}

	public boolean isTableBillsSyncInProgress() {
		return tableBillsTask != null;
	}

	public synchronized TableBillsResponse syncHistory() {
		TableBillsResponse response = null;
		if (isLoggedIn()) {
			Log.d("TableBillsTask", "need to request history");
			response = getRestServiceClient().getTableBills(
					userLogin.getAccountEmail(), userLogin.getPassword());

			if (!response.hasError() && response.getTableBills() != null) {
				markBillsReceived();
				tableBillsHistory
						.replaceAllTableBills(response.getTableBills());
			}

		}
		return response;
	}

	public synchronized void addTableBillToHistory(String waiter,
			int tableNumber) {
		tableBillsHistory.addTableBill(waiter, tableNumber, TBill.STATUS_OPEN);
	}
}
