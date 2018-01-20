/*
	Copyright 2018 Marceau Dewilde <m@ceau.be>
	
	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at
	
		https://www.apache.org/licenses/LICENSE-2.0
	
	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
*/
package be.ceau.podcastfinder.http;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;

import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;

class HttpClientFactory {

	private static final String USER_AGENT = "PodcastFinder/1.0.0-SNAPSHOT";

	/**
	 * Construct and return a new {@link HttpClient} instance.
	 * 
	 * @return {@link HttpClient}, not {@code null}
	 */
	HttpClient getHttpClient() {
		return HttpClientBuilder.create()
				.setConnectionManager(getConnectionManager())
				.setUserAgent(USER_AGENT)
				.setDefaultRequestConfig(getRequestConfig())
				.disableCookieManagement()
				.setSSLSocketFactory(getConnectionSocketFactory())
				.build();
	}

	private HttpClientConnectionManager getConnectionManager() {
		PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
		connectionManager.setMaxTotal(164);
		connectionManager.setDefaultMaxPerRoute(30);
		connectionManager.setValidateAfterInactivity(5 * 1000);
		return connectionManager;
	}

	private RequestConfig getRequestConfig() {
		return RequestConfig.custom()
				.setConnectTimeout(15 * 1000)
				.setConnectionRequestTimeout(2 * 60 * 1000)
				.setSocketTimeout(45 * 1000)
				.setCircularRedirectsAllowed(true)
				.setRedirectsEnabled(true)
				.setMaxRedirects(4)
				.setRelativeRedirectsAllowed(true)
				.build();
	}

	private SSLContext getSSLContext() {
		try {
			return SSLContextBuilder.create()
					.loadTrustMaterial(new TrustAllStrategy())
					.build();
		} catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
			throw new RuntimeException(e);
		}
	}

	private LayeredConnectionSocketFactory getConnectionSocketFactory() {
		return new SSLConnectionSocketFactory(getSSLContext(), NoopHostnameVerifier.INSTANCE);
	}

}
