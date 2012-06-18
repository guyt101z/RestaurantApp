package ro.gdg.android.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import ro.gdg.android.domain.AuthResponse;
import ro.gdg.android.domain.MenuResponse;
import ro.gdg.android.domain.RestaurantResponse;
import ro.gdg.android.domain.TableBillsResponse;
import ro.gdg.android.domain.UserLogin;
import ro.gdg.android.net.HttpRequestFactoryBuilder;
import ro.gdg.android.net.SignHandler;
import ro.gdg.android.net.SignInterceptor;
import android.util.Log;

public class RestServiceClient implements SignHandler {

	private static final String TAG = RestServiceClient.class.getSimpleName();

	public static final String SERVER_ADDRESS = "10.0.2.2";
	public static final String SERVER_API_URL = "http://" + SERVER_ADDRESS
			+ ":8084/RestaurantWeb/mobileApi/";

	// Check the user’s credentials, returns account info
	public static final String REQUEST_AUTH = "check.jsp";
	// Request a list of previously created table bills
	public static final String REQUEST_TABLE_BILLS = "tableBills.jsp";
	// Request a list of the menu categories and each's products
	private static final String REQUEST_MENU = "menu.jsp";

	private RestTemplate restTemplate;
	private UserLogin userCredentials;
	private HttpEntity<?> requestEntity;

	public RestServiceClient() {
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setContentType(new MediaType("application", "json"));
		requestHeaders.setAccept(Collections.singletonList(new MediaType(
				"application", "json")));
		this.requestEntity = new HttpEntity<Object>(requestHeaders);
	}

	synchronized RestTemplate getRestTemplate() {
		if (restTemplate == null) {
			restTemplate = new RestTemplate(
					HttpRequestFactoryBuilder
							.getHttpRequestFactory(new SignInterceptor(this)));
			GsonHttpMessageConverter messageConverter = new GsonHttpMessageConverter();
			List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
			messageConverters.add(messageConverter);
			restTemplate.setMessageConverters(messageConverters);
		}
		return restTemplate;
	}

	/**
	 * Check the user’s credentials and get account info
	 * 
	 * @param email
	 *            user's email
	 * @param password
	 *            user's password
	 * @returns an object of type {@link AuthResponse} containing authentication
	 *          status and account info
	 */
	public AuthResponse checkCredentials(String email, String password) {
		String url = SERVER_API_URL + REQUEST_AUTH + "?";
		Log.d(TAG, "checkCredentials url:" + url);

		try {
			ResponseEntity<AuthResponse> responseEntity = getRestTemplate()
					.exchange(url, HttpMethod.GET, requestEntity,
							AuthResponse.class);
			Log.d(TAG,
					"check credentials response : " + responseEntity.getBody());
			return responseEntity.getBody();
		} catch (HttpClientErrorException e) {
			Log.e(TAG, "check credentials request failed", e);
			Log.e(TAG, "status code: " + e.getStatusCode());
			AuthResponse response = new AuthResponse();
			if (HttpStatus.FORBIDDEN == e.getStatusCode()) {
				response.setErrorCode(RestaurantResponse.INVALID_CREDENTIALS_ERROR_CODE);
			} else {
				response.setErrorCode(RestaurantResponse.NETWORK_ERROR_CODE);
			}
			return response;
		} catch (HttpServerErrorException e) {
			Log.e(TAG, "check credentials request failed", e);
			Log.e(TAG, "status code: " + e.getStatusCode());
			AuthResponse response = new AuthResponse();
			response.setErrorCode(RestaurantResponse.SERVER_ERROR_CODE);
			return response;
		} catch (Exception e) {
			Log.e(TAG, "check credentials request failed", e);
			AuthResponse response = new AuthResponse();
			response.setErrorCode(RestaurantResponse.NETWORK_ERROR_CODE);
			return response;
		}
	}

	public void setUserCredentials(UserLogin userCredentials) {
		this.userCredentials = userCredentials;
	}

	@Override
	public String signRequest(String url, long timestamp) {
		if (userCredentials != null) {
			// the email should be combined with the timestamp with a colon
			// character
			// String content = timestamp + ":"
			// + userCredentials.getAccountEmail();
			// the user's password should be uppercased, then hashed using
			// SHA512 to
			// produce the key : key = SHA512(PASSWORD)
			// signature is computed as HMAC_SHA512(key, content)
			// String signature = Security.hashHmacSHA512(content,
			// Security.hashWithSHA512(userCredentials.getPassword()
			// .toUpperCase()));

			// return url + "email=" + userCredentials.getAccountEmail()
			// + "&timestamp=" + timestamp + "&signature=" + signature;

			// TODO encrypt password
			return url + "email=" + userCredentials.getAccountEmail()
					+ "&password=" + userCredentials.getPassword();
		} else {
			return url;
		}
	}

	/**
	 * Request a list of previously created table bills
	 * 
	 * @param email
	 *            user's email
	 * @param password
	 *            user's password
	 * @returns an object of type {@link TableBillsResponse} containing a list
	 *          of previously created table bills
	 */
	public TableBillsResponse getTableBills(String email, String password) {
		String url = SERVER_API_URL + REQUEST_TABLE_BILLS + "?";
		Log.d(TAG, "getTableBills url : " + url);

		try {
			ResponseEntity<TableBillsResponse> responseEntity = getRestTemplate()
					.exchange(url, HttpMethod.GET, requestEntity,
							TableBillsResponse.class);
			Log.d(TAG, "table bills response : " + responseEntity.getBody());
			return responseEntity.getBody();
		} catch (HttpClientErrorException e) {
			Log.e(TAG, "table bills request failed", e);
			Log.e(TAG, "status code: " + e.getStatusCode());
			TableBillsResponse response = new TableBillsResponse();
			if (HttpStatus.FORBIDDEN == e.getStatusCode()) {
				response.setErrorCode(RestaurantResponse.INVALID_CREDENTIALS_ERROR_CODE);
			} else {
				response.setErrorCode(RestaurantResponse.NETWORK_ERROR_CODE);
			}
			return response;
		} catch (HttpStatusCodeException e) {
			Log.e(TAG, "table bills request failed", e);
			TableBillsResponse response = new TableBillsResponse();
			response.setErrorCode(RestaurantResponse.SERVER_ERROR_CODE);
			return response;
		} catch (Exception e) {
			Log.e(TAG, "table bills request failed", e);
			TableBillsResponse response = new TableBillsResponse();
			response.setErrorCode(RestaurantResponse.NETWORK_ERROR_CODE);
			return response;
		}
	}

	/**
	 * Request a list of the menu categories and each's products
	 * 
	 * @param email
	 *            user's email
	 * @param password
	 *            user's password
	 * @returns an object of type {@link MenuResponse} containing a list of the
	 *          menu categories and each's products
	 */
	public MenuResponse getMenu(String email, String password) {
		String url = SERVER_API_URL + REQUEST_MENU + "?";
		Log.d(TAG, "getMenu url : " + url);

		try {
			ResponseEntity<MenuResponse> responseEntity = getRestTemplate()
					.exchange(url, HttpMethod.GET, requestEntity,
							MenuResponse.class);
			Log.d(TAG, "menu response : " + responseEntity.getBody());
			return responseEntity.getBody();
		} catch (HttpClientErrorException e) {
			Log.e(TAG, "menu request failed", e);
			Log.e(TAG, "status code: " + e.getStatusCode());
			MenuResponse response = new MenuResponse();
			if (HttpStatus.FORBIDDEN == e.getStatusCode()) {
				response.setErrorCode(RestaurantResponse.INVALID_CREDENTIALS_ERROR_CODE);
			} else {
				response.setErrorCode(RestaurantResponse.NETWORK_ERROR_CODE);
			}
			return response;
		} catch (HttpStatusCodeException e) {
			Log.e(TAG, "menu request failed", e);
			MenuResponse response = new MenuResponse();
			response.setErrorCode(RestaurantResponse.SERVER_ERROR_CODE);
			return response;
		} catch (Exception e) {
			Log.e(TAG, "menu request failed", e);
			MenuResponse response = new MenuResponse();
			response.setErrorCode(RestaurantResponse.NETWORK_ERROR_CODE);
			return response;
		}
	}

	@Override
	public boolean needToResendRequest(ClientHttpResponse response) {
		try {
			if (userCredentials != null
					&& response.getStatusCode() == HttpStatus.FORBIDDEN) {
				return true;
			}
		} catch (IOException e) {
			Log.e(TAG, "IO exception", e);
		}
		return false;
	}

}
