package ro.gdg.android.net;

import ro.gdg.android.domain.TableBillsResponse;

public interface TableBillsHistoryUpdateListener {

	void onSyncStarted();
	void onSyncFinished(TableBillsResponse response);
}
