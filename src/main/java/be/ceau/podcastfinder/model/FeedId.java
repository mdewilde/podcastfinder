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
package be.ceau.podcastfinder.model;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;

/**
 * The base information about a podcast as found by this library, containing name and feed uri.
 */
public final class FeedId implements Serializable {

	private static final long serialVersionUID = 1513409914011L;

	private final String name;
	private final URI uri;

	/**
	 * Constructor
	 * 
	 * @param name
	 *            {@link String}, can not be {@code null}
	 * @param uri
	 *            {@link String}, can not be {@code null}
	 * @throws NullPointerException
	 *             if either argument {@code null}
	 * @throws IllegalArgumentException
	 *             wrapping {@link URISyntaxException} if any
	 */
	public FeedId(String name, String uri) {
		Objects.requireNonNull(name);
		Objects.requireNonNull(uri);
		this.name = StringEscapeUtils.unescapeXml(name.trim());
		String normalized = StringUtils.removeEnd(uri.trim().toLowerCase(Locale.ENGLISH), "/");
		try {
			this.uri = new URI(normalized);
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * Constructor
	 * 
	 * @param name
	 *            {@link String}, can not be {@code null}
	 * @param uri
	 *            {@link URI}, can not be {@code null}
	 */
	public FeedId(String name, URI uri) {
		this(name, uri.toASCIIString());
	}

	/**
	 * The name of the podcast.
	 * 
	 * @return a {@link String}, not {@code null}
	 */
	public String getName() {
		return name;
	}

	/**
	 * The main link to the podcast feed.
	 * 
	 * @return a {@link URI}, not {@code null}
	 */
	public URI getUri() {
		return uri;
	}

	@Override
	public int hashCode() {
		return uri.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FeedId other = (FeedId) obj;
		if (!uri.equals(other.uri))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return new StringBuilder()
				.append("Podcast [name=")
				.append(name)
				.append(", uri=")
				.append(uri.toASCIIString())
				.append(", hashcode=")
				.append(hashCode())
				.append("]")
				.toString();
	}

}
