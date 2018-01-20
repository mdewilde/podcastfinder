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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Instances represent the result of a single successful download attempt for a given podcast
 */
public class FeedStatus implements Comparable<FeedStatus> {

	private final int podcastId;
	private final LocalDateTime date;
	private final LocalDate lastUpdate;
	private final int items;
	private final int hash;
	private final int bytes;

	/**
	 * Constructor
	 * 
	 * @param podcastId
	 *            {@code int} database id of the podcast, must be greater than 0
	 * @param date
	 *            {@link LocalDateTime} date of this status, can not be {@code null}
	 * @param lastUpdate
	 *            {@link LocalDate} date that the feed itself identifies as its last updated date
	 * @param items
	 *            {@code int} number of items or enclosures in the feed, must be 0 or greater
	 * @param hash
	 *            {@code int} hascode of the feed xml
	 * @param bytes
	 *            {@code int} size of the feed xml
	 */
	public FeedStatus(int podcastId, LocalDateTime date, LocalDate lastUpdate, int items, int hash, int bytes) {
		if (podcastId < 1) {
			throw new IllegalArgumentException("podcastId must be greater than 0");
		}
		Objects.requireNonNull(date);
		if (items < 0) {
			throw new IllegalArgumentException("number of items must be 0 or greater");
		}
		this.podcastId = podcastId;
		this.date = date;
		this.lastUpdate = lastUpdate;
		this.items = items;
		this.hash = hash;
		this.bytes = bytes;
	}

	public int getPodcastId() {
		return podcastId;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public LocalDate getLastUpdate() {
		return lastUpdate;
	}

	public int getItems() {
		return items;
	}

	public int getHash() {
		return hash;
	}

	public int getBytes() {
		return bytes;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + bytes;
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + hash;
		result = prime * result + items;
		result = prime * result + ((lastUpdate == null) ? 0 : lastUpdate.hashCode());
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
		FeedStatus other = (FeedStatus) obj;
		if (bytes != other.bytes)
			return false;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (hash != other.hash)
			return false;
		if (items != other.items)
			return false;
		if (lastUpdate == null) {
			if (other.lastUpdate != null)
				return false;
		} else if (!lastUpdate.equals(other.lastUpdate))
			return false;
		if (podcastId != other.podcastId)
			return false;
		return true;
	}

	/**
	 * Natural ordering of {@link FeedStatus} instances is on, in order:
	 * <ol>
	 * <li>natural order of {@code podcastId}
	 * <li>natural order of {@code date}
	 * </ol>
	 * 
	 * @param other
	 *            {@link FeedStatus} to compare with, can not be {@code null}
	 */
	@Override
	public int compareTo(FeedStatus other) {
		int cmp = this.podcastId - other.podcastId;
		if (cmp == 0) {
			return this.date.compareTo(other.date);
		}
		return cmp;
	}

}
