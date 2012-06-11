package ro.gdg.android.net;

import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.InterceptingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import android.os.Build;

public class HttpRequestFactoryBuilder {

	public static ClientHttpRequestFactory getHttpRequestFactory(SignInterceptor signInterceptor) {
		ClientHttpRequestFactory factory = null;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			factory = new SimpleClientHttpRequestFactory();
		} else {
			factory = new HttpComponentsClientHttpRequestFactory();
		}

		return new InterceptingClientHttpRequestFactory(factory,
				new ClientHttpRequestInterceptor[] { signInterceptor });
	}
}
