package ro.gdg.android.net;

import ro.gdg.android.domain.AuthResponse;

public interface CheckCredentialsListener {

	void done(AuthResponse response);

}
