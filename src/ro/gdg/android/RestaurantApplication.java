package ro.gdg.android;

import ro.gdg.android.api.RestaurantServiceClient;
import android.app.Application;
import android.util.Log;

public class RestaurantApplication extends Application {

	private static final String TAG = RestaurantApplication.class
			.getSimpleName();

	private RestaurantServiceClient restaurantServiceClient;

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "onCreate");
		restaurantServiceClient = new RestaurantServiceClient(this);
	}

	public RestaurantServiceClient getRestaurantServiceClient() {
		return restaurantServiceClient;
	}
}
