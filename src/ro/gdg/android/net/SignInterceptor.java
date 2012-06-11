package ro.gdg.android.net;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import android.util.Log;

public class SignInterceptor implements ClientHttpRequestInterceptor {

	private static final String TAG = SignInterceptor.class.getSimpleName();

	private SignHandler signHandler;
	private long serverTimeDelta = 0;

	public SignInterceptor(SignHandler signHandler) {
		this.signHandler = signHandler;
	}

	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body,
			ClientHttpRequestExecution execution) throws IOException {

		Log.i(TAG, "uri: " + request.getURI().toString());

		long currentTime = System.currentTimeMillis();
		long serverRequestTime = currentTime - serverTimeDelta;
		
		HttpSignedRequestWrapper signedRequest = new HttpSignedRequestWrapper(
				request, serverRequestTime);
		ClientHttpResponse response = execution.execute(signedRequest, body);

		long serverResponseTime = response.getHeaders().getDate();
		
		if (serverRequestTime + 30*60*1000 < serverResponseTime) {
			if (signHandler.needToResendRequest(response)) {
				 if (serverResponseTime != -1) {
					 serverTimeDelta = currentTime - serverResponseTime;
				 } 
				
				 currentTime = System.currentTimeMillis();
				 Log.e(TAG, "try again");
				 HttpSignedRequestWrapper newRequest = new HttpSignedRequestWrapper(
						 request, serverResponseTime);
				 response = execution.execute(newRequest, body);
				 serverResponseTime = response.getHeaders().getDate();
			}
		}

		if (serverResponseTime != -1) {
			serverTimeDelta = currentTime - serverResponseTime;
		}
		
		return response;
	}

	final class HttpSignedRequestWrapper implements HttpRequest {
		long timestamp;
		HttpRequest request;

		public HttpSignedRequestWrapper(HttpRequest request, long timestamp) {
			super();
			this.request = request;
			this.timestamp = timestamp;
		}

		@Override
		public HttpHeaders getHeaders() {
			return request.getHeaders();
		}

		@Override
		public HttpMethod getMethod() {
			return request.getMethod();
		}

		@Override
		public URI getURI() {
			String signedURL = signHandler.signRequest(request.getURI()
					.toString(), timestamp);
			Log.i(TAG, "signedURI: " +signedURL);
			try {
				return new URI(signedURL);
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return request.getURI();
		}
	};
}
