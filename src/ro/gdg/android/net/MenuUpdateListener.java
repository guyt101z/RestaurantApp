package ro.gdg.android.net;

import ro.gdg.android.domain.MenuResponse;

public interface MenuUpdateListener {

	void onSyncStarted();
	void onSyncFinished(MenuResponse response);
}
