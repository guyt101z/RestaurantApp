package ro.gdg.android.net;

import org.springframework.http.client.ClientHttpResponse;

public interface SignHandler {
	String signRequest(String url, long timestamp);
	boolean needToResendRequest(ClientHttpResponse response);
}