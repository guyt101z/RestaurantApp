package ro.gdg.android;

import ro.gdg.android.api.RestaurantServiceClient;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;

public class TablesActivity extends Activity {
	private static final String TAG = TablesActivity.class.getSimpleName();

	RestaurantServiceClient serviceClient;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);

		Log.d(TAG, "onCreate");
		this.setContentView(R.layout.tables);

		serviceClient = ((RestaurantApplication) getApplication())
				.getRestaurantServiceClient();

		if (!serviceClient.isLoggedIn()) {
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
		}
	}

	// handles clicks on the dashboard buttons
	public void goTo(View v) {
		// Intent i = new Intent(this, CategoryRecipesListActivity.class);
		int extra = 0;
		boolean noOrdersYet = false;
		switch (v.getId()) {
		case R.id.btn_table1: {
			extra = 1;
		}
			break;
		case R.id.btn_table2: {
			extra = 2;
		}
			break;
		case R.id.btn_table3: {
			extra = 3;
		}
			break;
		case R.id.btn_table4: {
			extra = 4;
		}
			break;
		case R.id.btn_table5: {
			extra = 5;
		}
			break;
		case R.id.btn_table6: {
			extra = 6;
		}
			break;
		case R.id.btn_table7: {
			extra = 7;
		}
			break;
		case R.id.btn_table8: {
			extra = 8;
		}
			break;
		case R.id.btn_table9: {
			extra = 9;
		}
			break;
		}
		// i.putExtra(CategoryRecipesListActivity.SELECT_TAB_EXTRA, extra);
		// startActivity(i);
	}
}
