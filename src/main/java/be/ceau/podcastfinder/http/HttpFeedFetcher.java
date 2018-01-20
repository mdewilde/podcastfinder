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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;

import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.AbstractResponseHandler;

import be.ceau.podcastparser.PodcastParser;
import be.ceau.podcastparser.models.core.Feed;

/**
 * {@link HttpFetcher} implementation that directly parses the response body into a {@link Feed}
 */
public class HttpFeedFetcher implements HttpFetcher<Feed> {

	private final HttpClient httpClient = new HttpClientFactory().getHttpClient();
	private final FeedReader feedReader = new FeedReader();

	/**
	 * Retrieve the given {@link URI} with an HTTP GET request. Return the response body as {@link Feed}
	 * 
	 * @param uri
	 *            {@link URI}, not {@code null}
	 * @return instance of {@link Feed}, not {@code null}
	 */
	@Override
	public Feed get(URI uri) throws IOException {
		return httpClient.execute(new HttpGet(uri), feedReader);
	}

	private static final class FeedReader extends AbstractResponseHandler<Feed> {

		private final PodcastParser podcastParser = new PodcastParser();

		@Override
		public Feed handleEntity(final HttpEntity entity) throws IOException {
			final InputStream in = entity.getContent();
			if (in == null) {
				return null;
			}
			try {
				Reader reader = new InputStreamReader(entity.getContent(), getCharset(entity));
				return podcastParser.parse(reader);
			} finally {
				in.close();
			}
		}

		private Charset getCharset(final HttpEntity entity) {
			try {
				ContentType c = ContentType.get(entity);
				if (c != null && c.getCharset() != null) {
					return c.getCharset();
				}
			} catch (UnsupportedCharsetException | ParseException e) {

			}
			return StandardCharsets.UTF_8;
		}

	}

}
