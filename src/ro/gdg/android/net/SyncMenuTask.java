package ro.gdg.android.net;

import ro.gdg.android.api.RestaurantServiceClient;
import ro.gdg.android.domain.MenuResponse;
import android.os.AsyncTask;

public class SyncMenuTask extends AsyncTask<String, Void, MenuResponse> {

	RestaurantServiceClient serviceClient;

	public SyncMenuTask(RestaurantServiceClient serviceClient) {
		this.serviceClient = serviceClient;
	}

	@Override
	protected MenuResponse doInBackground(String... params) {
		MenuResponse response = null;

		if (serviceClient != null) {
			return serviceClient.synchronizeMenu();
		}
		return response;
	}

	@Override
	protected void onPostExecute(MenuResponse response) {
		if (serviceClient != null) {
			serviceClient.done(response);
		}
	}

}
