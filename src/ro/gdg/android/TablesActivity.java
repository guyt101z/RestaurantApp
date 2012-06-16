package ro.gdg.android;

import java.util.ArrayList;

import ro.gdg.android.api.RestaurantServiceClient;
import ro.gdg.android.domain.TableBillsResponse;
import ro.gdg.android.net.TableBillsHistoryUpdateListener;
import ro.gdg.android.util.ErrorHandler;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;

public class TablesActivity extends Activity implements
		TableBillsHistoryUpdateListener {

	private static final String TAG = TablesActivity.class.getSimpleName();

	public static final String TABLE_NO_EXTRA = "table_no_extra";
	public static final int TABLE_FREE = 0;
	public static final int TABLE_OCCUPIED_MINE = 1;
	public static final int TABLE_OCCUPIED_OTHER = 2;

	RestaurantServiceClient serviceClient;
	ProgressBar bannerProgressBar;
	Button table1Btn, table2Btn, table3Btn, table4Btn, table5Btn, table6Btn,
			table7Btn, table8Btn, table9Btn;
	ArrayList<Button> tableButtons;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);

		Log.d(TAG, "onCreate");
		this.setContentView(R.layout.tables);

		initButtons();
		bannerProgressBar = (ProgressBar) findViewById(R.id.bannerProgressBar);

		serviceClient = ((RestaurantApplication) getApplication())
				.getRestaurantServiceClient();

		if (!serviceClient.isLoggedIn()) {
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
			finish();
		} else {
			refreshTables();
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		serviceClient.setTableBillsListener(this);
		serviceClient.syncTableBills();
	}

	@Override
	public void onStop() {
		super.onStop();
		Log.d(TAG, "onStop");
		serviceClient.setTableBillsListener(null);
		setProgressVisible(false);
	}

	@Override
	public void onSyncStarted() {
		Log.d(TAG, "onSyncStarted");
		setProgressVisible(true);
	}

	@Override
	public void onSyncFinished(TableBillsResponse response) {
		Log.d(TAG, "onSyncFinished");
		setProgressVisible(false);

		if (response == null) {
			Log.d(TAG, "onSyncFinished response is null ");
			return;
		}

		if (response.hasError()) {
			Log.d(TAG, "onSyncFinished response has error: " + response.getErrorCode());
			ErrorHandler.checkAndShowError(this, response, null);
		} else {
			Log.d(TAG, "onSyncFinished successfull");
			refreshTables();
		}
	}

	// handles clicks on the dashboard buttons
	public void goTo(View v) {
		Intent i;
		int tableNo = 0;

		switch (v.getId()) {
		case R.id.btn_table1:
			tableNo = 1;
			break;
		case R.id.btn_table2:
			tableNo = 2;
			break;
		case R.id.btn_table3:
			tableNo = 3;
			break;
		case R.id.btn_table4:
			tableNo = 4;
			break;
		case R.id.btn_table5:
			tableNo = 5;
			break;
		case R.id.btn_table6:
			tableNo = 6;
			break;
		case R.id.btn_table7:
			tableNo = 7;
			break;
		case R.id.btn_table8:
			tableNo = 8;
			break;
		case R.id.btn_table9:
			tableNo = 9;
			break;
		}
		if (serviceClient.hasOpenBill(tableNo)) {
			i = new Intent(this, TableBillActivity.class);
		} else {
			i = new Intent(this, MenuActivity.class);
		}
		i.putExtra(TABLE_NO_EXTRA, tableNo);
		startActivity(i);
	}

	private void initButtons() {
		tableButtons = new ArrayList<Button>();
		table1Btn = (Button) findViewById(R.id.btn_table1);
		tableButtons.add(table1Btn);
		table2Btn = (Button) findViewById(R.id.btn_table2);
		tableButtons.add(table2Btn);
		table3Btn = (Button) findViewById(R.id.btn_table3);
		tableButtons.add(table3Btn);
		table4Btn = (Button) findViewById(R.id.btn_table4);
		tableButtons.add(table4Btn);
		table5Btn = (Button) findViewById(R.id.btn_table5);
		tableButtons.add(table5Btn);
		table6Btn = (Button) findViewById(R.id.btn_table6);
		tableButtons.add(table6Btn);
		table7Btn = (Button) findViewById(R.id.btn_table7);
		tableButtons.add(table7Btn);
		table8Btn = (Button) findViewById(R.id.btn_table8);
		tableButtons.add(table8Btn);
		table9Btn = (Button) findViewById(R.id.btn_table9);
		tableButtons.add(table9Btn);
	}

	private void refreshTables() {
		Log.d(TAG, "onSyncFinished successfull");
		int no = 1;
		for (Button btn : tableButtons) {
			switch (serviceClient.getTableState(no)) {
			case TABLE_OCCUPIED_MINE:
				btn.setEnabled(true);
				btn.setBackgroundResource(R.drawable.btn_red_selector);
				break;
			case TABLE_OCCUPIED_OTHER:
				btn.setEnabled(false);
				break;
			case TABLE_FREE:
				btn.setEnabled(true);
				btn.setBackgroundResource(R.drawable.btn_green_selector);
				break;
			default:
				break;
			}
			no++;
		}
	}

	/**
	 * Set visibility of progress bar
	 * 
	 * @param visible
	 */
	public void setProgressVisible(boolean visible) {
		if (bannerProgressBar != null) {
			bannerProgressBar.setVisibility(visible ? View.VISIBLE : View.GONE);
		}
	}
	
	public void refresh(View view){
		serviceClient.startTableBillsTask();
	}
	
	public void logout(View view){
		Intent intent = new Intent(this, LogoutActivity.class);
		startActivity(intent);
	}
}
