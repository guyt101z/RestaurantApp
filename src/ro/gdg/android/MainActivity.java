package ro.gdg.android;

import ro.gdg.android.api.RestaurantServiceClient;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

public class MainActivity extends Activity {
	private static final String TAG = MainActivity.class.getSimpleName();

	RestaurantServiceClient serviceClient;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);

		Log.d(TAG, "onCreate");
		this.setContentView(R.layout.main);

		serviceClient = ((RestaurantApplication) getApplication())
				.getRestaurantServiceClient();

		if (!serviceClient.isLoggedIn()) {
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
		}
	}
}
