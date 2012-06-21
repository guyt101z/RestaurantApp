package ro.gdg.android;

import ro.gdg.android.domain.AuthResponse;
import ro.gdg.android.domain.UserLogin;
import ro.gdg.android.net.CheckCredentialsListener;
import ro.gdg.android.net.CheckCredentialsTask;
import ro.gdg.android.util.ErrorHandler;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

public class TableBillActivity extends Activity implements
		CheckCredentialsListener {

	static final String TAG = TableBillActivity.class.getSimpleName();

	static final int DIALOG_PROGRESS_REQ = 0;

	volatile CheckCredentialsTask currentTask;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);

		setContentView(R.layout.table_bill);

	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		switch (id) {
		case DIALOG_PROGRESS_REQ:
			ProgressDialog pd = new ProgressDialog(this);
			pd.setMessage(getString(R.string.progress_requesting));
			pd.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					if (currentTask != null) {
						currentTask.cancel(true);
					}
				}
			});
			dialog = pd;
			break;
		default:
			dialog = null;
			break;
		}
		return dialog;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (currentTask != null) {
			currentTask.setListener(null);
			currentTask = null;
		}
	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		return currentTask;
	}

	/**
	 * call this in {@link #onCreate(android.os.Bundle)} method
	 */
	void checkTask() {
		currentTask = (CheckCredentialsTask) this
				.getLastNonConfigurationInstance();
		if (currentTask != null) {
			currentTask.setListener(this);
		}
	}

	void startLoginTask(UserLogin login) {
		showDialog(DIALOG_PROGRESS_REQ);
		currentTask = new CheckCredentialsTask(
				((RestaurantApplication) getApplication())
						.getRestaurantServiceClient());
		currentTask.setListener(this);
		currentTask.execute(login);
	}

	@Override
	public void done(AuthResponse response) {
		removeDialog(DIALOG_PROGRESS_REQ);
		currentTask = null;
		if (response == null) {
			// Canceled
			return;
		}

		if (!ErrorHandler.checkAndShowError(this, response, null)) {
			launchMain();
		}
	}

	void launchMain() {
		Intent intent = new Intent(this, TablesActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		finish();
	}
}
