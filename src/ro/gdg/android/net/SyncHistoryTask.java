package ro.gdg.android.net;

import ro.gdg.android.api.RestaurantServiceClient;
import ro.gdg.android.domain.TableBillsResponse;
import android.os.AsyncTask;

public class SyncHistoryTask extends AsyncTask<String, Void, TableBillsResponse> {

	RestaurantServiceClient serviceClient;

	public SyncHistoryTask(RestaurantServiceClient serviceClient) {
		this.serviceClient = serviceClient;
	}

	@Override
	protected TableBillsResponse doInBackground(String... params) {
		TableBillsResponse response = null;

		if (serviceClient != null) {
			return serviceClient.syncHistory();
		}
		return response;
	}

	@Override
	protected void onPostExecute(TableBillsResponse response) {
		if (serviceClient != null) {
			serviceClient.done(response);
		}
	}
}