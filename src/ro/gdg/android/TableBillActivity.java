package ro.gdg.android;

import ro.gdg.android.domain.AuthResponse;
import ro.gdg.android.domain.UserLogin;
import ro.gdg.android.net.CheckCredentialsListener;
import ro.gdg.android.net.CheckCredentialsTask;
import ro.gdg.android.ui.adapter.OrderedProductsListAdapter;
import ro.gdg.android.util.ErrorHandler;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

public class TableBillActivity extends ListActivity implements
		CheckCredentialsListener {

	static final String TAG = TableBillActivity.class.getSimpleName();
	public static final String EXTRA_TABLE_BILL_ID = "extra_table_bill_id";
	public static final String EXTRA_TOTAL = "extra_total";

	static final int DIALOG_PROGRESS_REQ = 0;

	volatile CheckCredentialsTask currentTask;
	private OrderedProductsListAdapter listAdapter;
	private long tableBillID;
	private int total;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);

		setContentView(R.layout.table_bill);

		tableBillID = getIntent().getLongExtra(EXTRA_TABLE_BILL_ID, -1);
		Log.d(TAG, "tableBill id = " + tableBillID);
		total = getIntent().getIntExtra(EXTRA_TOTAL, 0);
		Log.d(TAG, "tableBill total = " + total);
		((TextView) findViewById(R.id.actionbarTotalValue)).setText(total + "");

		Cursor cursor = ((RestaurantApplication) getApplication())
				.getRestaurantServiceClient().getOrderedProductsOfBill(
						tableBillID);
		if (listAdapter != null) {
			// stopManagingCursor(listAdapter.getCursor());
			listAdapter.changeCursor(cursor);
		} else {
			listAdapter = new OrderedProductsListAdapter(this,
					((RestaurantApplication) getApplication())
							.getRestaurantServiceClient()
							.getTableBillsHistory(), R.layout.product_ordered,
					cursor, false);
			setListAdapter(listAdapter);
		}
		startManagingCursor(cursor);

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

	public void add(View view) {
		// TODO
		// save already ordered products, total and tableID
		// go to menu for result
	}

	public void payment(View view) {
		// TODO
		// set status of table_bill to closed
		// send request to server to set status and print receipt
		// set lastReceived history to 0 to refresh dashboard
		launchMain();
	}

	public void deleteBill(View view) {
		// TODO
		// delete bill from db
		// send request to server to delete bill
		// go to dashboard - refresh
		// finish this
	}

	public void done(View view) {
		// TODO
		// send request to server to add/update table bill
		// set time to 0 to refresh tables
		launchMain();
	}
}
