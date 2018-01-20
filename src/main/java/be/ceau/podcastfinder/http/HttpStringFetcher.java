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

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;

import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.util.EntityUtils;

/**
 * {@link HttpFetcher} implementation that saves the response body into a {@link String}
 */
public class HttpStringFetcher implements HttpFetcher<String> {

	private final HttpClient httpClient = new HttpClientFactory().getHttpClient();
	private final StringReader stringReader = new StringReader();

	/**
	 * Retrieve the given {@link URI} with an HTTP GET request. Return the response body as {@link String}
	 * 
	 * @param uri
	 *            {@link URI}, not {@code null}
	 * @return {@link String}, not {@code null}
	 */
	@Override
	public String get(URI uri) throws IOException {
		return httpClient.execute(new HttpGet(uri), stringReader);
	}

	private static final class StringReader extends BasicResponseHandler {
		
	    @Override
	    public String handleEntity(final HttpEntity entity) throws IOException {
	    	return EntityUtils.toString(entity, StandardCharsets.UTF_8);
	    }

	}

}
