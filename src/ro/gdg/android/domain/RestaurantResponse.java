package ro.gdg.android.domain;

public class RestaurantResponse {

	public static final int NO_ERROR = 0;
	public static final int SERVER_ERROR_CODE = 1;
	public static final int NETWORK_ERROR_CODE = 2;
	public static final int INVALID_CREDENTIALS_ERROR_CODE = 3;
	public static final int INVALID_VIN_ERROR_CODE = 4;
	public static final int INVALID_PLATE_STATE_ERROR_CODE = 5;

	private int errorCode;

	public RestaurantResponse() {
		this.errorCode = NO_ERROR;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public boolean hasError() {
		boolean hasError = false;
		switch (this.errorCode) {
		case NO_ERROR:
			hasError = false;
			break;
		case SERVER_ERROR_CODE:
			hasError = true;
			break;
		case NETWORK_ERROR_CODE:
			hasError = true;
			break;
		case INVALID_CREDENTIALS_ERROR_CODE:
			hasError = true;
			break;
		}
		return hasError;
	}

}
