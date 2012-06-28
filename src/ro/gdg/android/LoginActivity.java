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
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends Activity implements CheckCredentialsListener {

	static final String TAG = LoginActivity.class.getSimpleName();

	static final int DIALOG_PROGRESS_REQ = 0;

	EditText loginUserText;
	EditText loginPasswordText;

	volatile CheckCredentialsTask currentTask;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);

		setContentView(R.layout.login);

		Button loginBtn = (Button) findViewById(R.id.btnLogin);
		loginUserText = (EditText) findViewById(R.id.loginUser);
		loginPasswordText = (EditText) findViewById(R.id.loginPassword);

		loginUserText.addTextChangedListener(new EnableIfLengthWatcher(
				loginBtn, loginUserText, loginPasswordText));
		loginPasswordText.addTextChangedListener(new EnableIfLengthWatcher(
				loginBtn, loginUserText, loginPasswordText));

		// loginUserText.setText("ghele_diana@yahoo.com");
		// loginPasswordText.setText("123123");

		loginUserText.setTypeface(Typeface.DEFAULT);
		loginPasswordText.setTypeface(Typeface.DEFAULT);

		checkLoginTask();
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
	void checkLoginTask() {
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

	public void loginAction(View view) {

		String name = loginUserText.getText().toString();
		String password = loginPasswordText.getText().toString();

		Log.i(TAG, "login " + name + ":" + password);

		startLoginTask(new UserLogin(name, password));
	}

	void launchMain() {
		Intent intent = new Intent(this, TablesActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}

	static class EnableIfLengthWatcher implements TextWatcher {
		View view;
		EditText userInput;
		EditText passwordInput;

		EnableIfLengthWatcher(View view, EditText userInput,
				EditText passwordInput) {
			this.view = view;
			this.userInput = userInput;
			this.passwordInput = passwordInput;
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
		}

		@Override
		public void afterTextChanged(Editable s) {
			view.setEnabled(userInput.getText().length() != 0
					&& passwordInput.getText().length() != 0);
		}
	}

}
