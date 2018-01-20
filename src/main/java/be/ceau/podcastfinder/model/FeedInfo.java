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
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

/**
 * Information about a podcast that can be retrieved by this library.
 */
public final class FeedInfo implements Serializable {

	private static final long serialVersionUID = 1513711420112L;

	private final FeedId feedId;
	private final String language;
	private final String description;

	/**
	 * Constructor
	 * 
	 * @param feedId
	 *            {@link FeedId}, can not be {@code null}
	 * @param language
	 *            {@link String}, can be {@code null}
	 * @param description
	 *            {@link String}, can be {@code null}
	 */
	public FeedInfo(FeedId feedId, String language, String description) {
		Objects.requireNonNull(feedId);
		this.feedId = feedId;
		this.language = language;
		this.description = description;
	}

	/**
	 * The podcast's core information
	 * 
	 * @return {@link FeedId}, never {@code null}
	 */
	public FeedId getFeedId() {
		return feedId;
	}


	/**
	 * The podcast's language, as listed in its feed. Will be {@code null} if the podcast was never retrieved or if its feed
	 * does not contain the language.
	 * 
	 * @return {@link String}, possibly {@code null}
	 */
	public String getLanguage() {
		return language;
	}

	/**
	 * @return {@code true} if a language is set in this instance, {@code false} otherwise
	 */
	public boolean isLanguageKnown() {
		return StringUtils.length(language) > 1;
	}
	
	/**
	 * @return {@code true} if a language is not set in this instance, {@code false} otherwise
	 */
	public boolean isLanguageUnknown() {
		return StringUtils.length(language) < 1;
	}
	
	/**
	 * The podcast's description, as given in its feed.
	 * 
	 * @return {@link String}, possibly {@code null}
	 */
	public String getDescription() {
		return description;
	}

	@Override
	public int hashCode() {
		return feedId.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FeedInfo other = (FeedInfo) obj;
		if (!feedId.equals(other.feedId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return new StringBuilder()
				.append("FullPodcastLink [feedId=")
				.append(feedId)
				.append(", language=")
				.append(language)
				.append(", description=")
				.append(description)
				.append("]")
				.toString();
	}

}
