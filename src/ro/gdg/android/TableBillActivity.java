package ro.gdg.android;

import ro.gdg.android.api.RestaurantServiceClient;
import ro.gdg.android.domain.AuthResponse;
import ro.gdg.android.domain.UserLogin;
import ro.gdg.android.net.CheckCredentialsListener;
import ro.gdg.android.net.CheckCredentialsTask;
import ro.gdg.android.ui.adapter.OrderedProductsListAdapter;
import ro.gdg.android.util.ErrorHandler;
import android.app.AlertDialog;
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
import android.widget.AdapterView;
import android.widget.TextView;

public class TableBillActivity extends ListActivity implements
		CheckCredentialsListener {

	static final String TAG = TableBillActivity.class.getSimpleName();
	public static final String EXTRA_TABLE_BILL_ID = "extra_table_bill_id";

	static final int DIALOG_PROGRESS_REQ = 0;
	static final int DIALOG_DELETE = 1;
	static final int ADD_PRODUCTS = 2;

	private RestaurantServiceClient serviceClient;
	volatile CheckCredentialsTask currentTask;
	private OrderedProductsListAdapter listAdapter;
	private long tableBillID;
	private int total;
	private long selectedProduct;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);

		setContentView(R.layout.table_bill);
		serviceClient = ((RestaurantApplication) getApplication())
				.getRestaurantServiceClient();

		tableBillID = getIntent().getLongExtra(EXTRA_TABLE_BILL_ID, -1);
		Log.d(TAG, "tableBill id = " + tableBillID);

		refreshTotal();

		Cursor cursor = serviceClient.getOrderedProductsOfBill(tableBillID);
		if (listAdapter != null) {
			// stopManagingCursor(listAdapter.getCursor());
			listAdapter.changeCursor(cursor);
		} else {
			listAdapter = new OrderedProductsListAdapter(this,
					serviceClient.getTableBillsHistory(),
					R.layout.product_ordered, cursor, false);
			setListAdapter(listAdapter);
		}

		getListView().setOnItemLongClickListener(
				new AdapterView.OnItemLongClickListener() {

					@Override
					public boolean onItemLongClick(AdapterView<?> arg0, View v,
							int pos, long id) {
						return onLongListItemClick(v, pos, id);
					}
				});
		startManagingCursor(cursor);

	}

	protected boolean onLongListItemClick(View v, int pos, long id) {
		Log.i(TAG, "onLongListItemClick id = " + id);
		selectedProduct = id;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Are you sure you want to delete it from the order?")
				.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// delete product
								serviceClient
										.deleteOrderedProduct(selectedProduct);
								listAdapter.notifyDataSetChanged();

								// update total
								refreshTotal();
							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				}).create().show();
		return true;
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
		Intent intent = new Intent(this, MenuActivity.class);
		intent.putExtra(EXTRA_TABLE_BILL_ID, tableBillID);
		startActivityForResult(intent, ADD_PRODUCTS);
	}

	public void payment(View view) {
		// TODO
		// set status of table_bill to closed
		serviceClient.closeTableBill(tableBillID);

		// send request to server to set status and print receipt

		// go to dashboard
		launchMain();
	}

	public void deleteBill(View view) {
		// TODO
		// delete bill from db
		serviceClient.deleteTableBill(tableBillID);

		// send request to server to delete bill

		// go to dashboard
		launchMain();
	}

	public void done(View view) {
		// TODO how to know if new or just update? -> check on server if already
		// exists

		// send request to server to add/update table bill

		// go to dashboard
		launchMain();
	}

	@Override
	public void onBackPressed() {
		// TODO think about when is already added with done -> treat as if done
		// was clicked

		// delete from db
		serviceClient.deleteTableBill(tableBillID);
		super.onBackPressed();
	}

	public void refreshTotal() {
		total = serviceClient.getOrderedProductsTotalOfBill(tableBillID);
		Log.d(TAG, "tableBill total = " + total);
		((TextView) findViewById(R.id.actionbarTotalValue)).setText(total + "");
	}

}
