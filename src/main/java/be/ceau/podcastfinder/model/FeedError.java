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

import java.time.LocalDateTime;
import java.util.Objects;

import be.ceau.podcastparser.util.Strings;

/**
 * Instances represent the result of a single failed download attempt for a given podcast
 */
public class FeedError implements Comparable<FeedError> {

	private final int podcastId;
	private final LocalDateTime date;
	private final String error;
	private final String message;

	/**
	 * Constructor
	 * 
	 * @param podcastId
	 *            {@code int} database id of podcast, must be greater than 0
	 * @param date
	 *            {@link LocalDateTime} the error occurred, can not be {@code null}
	 * @param error
	 *            error {@link String}, can not be {@code blank}
	 * @param message
	 *            {@link String} additional information about the error, can be {@code null}
	 */
	public FeedError(int podcastId, LocalDateTime date, String error, String message) {
		if (podcastId < 1) {
			throw new IllegalArgumentException("podcastId must be greater than 0");
		}
		Objects.requireNonNull(date);
		Strings.requireNonBlank(error);
		this.podcastId = podcastId;
		this.date = date;
		this.error = error;
		this.message = message;
	}

	public int getPodcastId() {
		return podcastId;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public String getError() {
		return error;
	}

	public String getMessage() {
		return message;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + ((error == null) ? 0 : error.hashCode());
		result = prime * result + ((message == null) ? 0 : message.hashCode());
		result = prime * result + podcastId;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FeedError other = (FeedError) obj;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (error == null) {
			if (other.error != null)
				return false;
		} else if (!error.equals(other.error))
			return false;
		if (message == null) {
			if (other.message != null)
				return false;
		} else if (!error.equals(other.message))
			return false;
		if (podcastId != other.podcastId)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return new StringBuilder()
				.append("FeedError [podcastId=")
				.append(podcastId)
				.append(", date=")
				.append(date)
				.append(", error=")
				.append(error)
				.append(", message=")
				.append(message)
				.append("]")
				.toString();
	}

	/**
	 * Natural ordering of {@link FeedError} instances is on, in order:
	 * <ol>
	 * <li>natural order of {@code podcastId}
	 * <li>natural order of {@code date}
	 * </ol>
	 * 
	 * @param other
	 *            {@link FeedError} to compare with, can not be {@code null}
	 */
	@Override
	public int compareTo(FeedError other) {
		int cmp = this.podcastId - other.podcastId;
		if (cmp == 0) {
			return this.date.compareTo(other.date);
		}
		return cmp;
	}

}
