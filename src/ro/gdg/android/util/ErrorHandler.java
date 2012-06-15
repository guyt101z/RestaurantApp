package ro.gdg.android.util;

import ro.gdg.android.R;
import ro.gdg.android.domain.RestaurantResponse;
import android.app.Activity;
import android.view.Gravity;
import android.widget.Toast;

public class ErrorHandler {

	public static boolean checkAndShowError(Activity activity,
			RestaurantResponse response, String textParam) {

		if (response.hasError()) {
			switch (response.getErrorCode()) {
			case RestaurantResponse.NETWORK_ERROR_CODE:
				showToast(
						activity,
						activity.getBaseContext().getString(
								R.string.msg_network_error));
				break;
			case RestaurantResponse.INVALID_CREDENTIALS_ERROR_CODE:
				showToast(
						activity,
						activity.getBaseContext().getString(
								R.string.msg_invalid_credentials));

				break;
			case RestaurantResponse.SERVER_ERROR_CODE:
				showToast(
						activity,
						activity.getBaseContext().getString(
								R.string.msg_service_unavailable));

				break;
			default:
				break;
			}
			return true;
		}
		return false;
	}

	private static void showToast(Activity activity, String text) {
		Toast toast = Toast.makeText(activity, text, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}
}
