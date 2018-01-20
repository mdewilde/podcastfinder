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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Instances represent the full knowledge of this library about a given podcast, including
 * information about any download attempts.
 */
public class PersistedFeed {

	private final int podcastId;
	private final FeedInfo feedInfo;
	private final List<FeedStatus> feedStatuses = new ArrayList<>();
	private final List<FeedError> feedErrors = new ArrayList<>();

	/**
	 * Constructor
	 * 
	 * @param podcastId
	 *            {@code int} database id of podcast, must be greater than 0
	 * @param feedInfo
	 *            {@link FeedInfo} for the podcast, can not be {@code null}
	 */
	public PersistedFeed(int podcastId, FeedInfo feedInfo) {
		if (podcastId < 1) {
			throw new IllegalArgumentException("podcastId must be greater than 0");
		}
		Objects.requireNonNull(feedInfo);
		this.podcastId = podcastId;
		this.feedInfo = feedInfo;
	}

	/**
	 * @return the database id {@code int} of this podcast
	 */
	public int getPodcastId() {
		return podcastId;
	}

	/**
	 * @return {@link FeedId} of this podcast, never {@code null}
	 */
	public FeedId getFeedId() {
		return feedInfo.getFeedId();
	}

	/**
	 * @return {@link FeedInfo} for this podcast, never {@code null}
	 */
	public FeedInfo getFeedInfo() {
		return feedInfo;
	}

	/**
	 * @return unmodifiable {@link List} with every {@link FeedStatus} instance, never {@code null}
	 */
	public List<FeedStatus> getFeedStatuses() {
		return Collections.unmodifiableList(feedStatuses);
	}

	public void addFeedStatus(FeedStatus feedStatus) {
		if (feedStatus != null) {
			feedStatuses.add(feedStatus);
		}
	}

	public FeedStatus getNewestFeedStatus() {
		return feedStatuses.stream()
				.sorted(Comparator.reverseOrder())
				.findFirst()
				.orElse(null);
	}
	
	/**
	 * @return unmodifiable {@link List} with every {@link FeedError} instance, never {@code null}
	 */
	public List<FeedError> getFeedErrors() {
		return Collections.unmodifiableList(feedErrors);
	}

	public void addFeedError(FeedError feedError) {
		if (feedError != null) {
			feedErrors.add(feedError);
		}
	}

	public FeedError getNewestFeedError() {
		return feedErrors.stream()
				.sorted(Comparator.reverseOrder())
				.findFirst()
				.orElse(null);
	}

	/**
	 * {@link PersistedFeed} hashcode is based on {@code podcastId} only
	 */
	@Override
	public int hashCode() {
		return 31 + podcastId;
	}

	/**
	 * {@link PersistedFeed} equals is based on {@code podcastId} only
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PersistedFeed other = (PersistedFeed) obj;
		if (podcastId != other.podcastId)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "PersistedFeed [podcastId=" + podcastId + ", feedInfo=" + feedInfo + ", feedStatuses=" + feedStatuses
				+ ", feedErrors=" + feedErrors + "]";
	}

}
