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
package be.ceau.podcastfinder.update;

import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Queue;
import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.HttpResponseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.ceau.podcastfinder.http.HttpFeedFetcher;
import be.ceau.podcastfinder.model.FeedError;
import be.ceau.podcastfinder.model.FeedId;
import be.ceau.podcastfinder.model.FeedInfo;
import be.ceau.podcastfinder.model.FeedStatus;
import be.ceau.podcastfinder.model.PersistedFeed;
import be.ceau.podcastparser.models.core.Feed;
import util.hash.MurmurHash3;

/**
 * <p>
 * Logic to download a particular podcast feed and process its contents.
 * </p>
 * <p>
 * The actual nature of the processing can be customized by providing a {@link Consumer} of
 * {@link PersistedFeed} instances.
 * </p>
 */
public class PipelineDownloader implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(PipelineDownloader.class);

	private final PersistedFeed persistedFeed;
	private final Queue<PersistedFeed> queue;
	private final HttpFeedFetcher httpFeedFetcher;
	
	public PipelineDownloader(PersistedFeed feed, Queue<PersistedFeed> queue, HttpFeedFetcher httpFeedFetcher) {
		Objects.requireNonNull(feed);
		Objects.requireNonNull(queue);
		this.persistedFeed = feed;
		this.queue = queue;
		this.httpFeedFetcher = httpFeedFetcher;
	}
	
	@Override
	public void run() {
		PersistedFeed newPersistedFeed = executeLogic();
		queue.add(newPersistedFeed);
	}
	
	private PersistedFeed executeLogic() {
		try {
			Feed feed = httpFeedFetcher.get(persistedFeed.getFeedId().getUri());
			return process(feed);
		} catch (Exception e) {
			return process(e);
		}
	}

	private PersistedFeed process(Feed feed) throws Exception {
		FeedInfo feedInfo = enrich(feed);
		PersistedFeed newPersistedFeed = new PersistedFeed(persistedFeed.getPodcastId(), feedInfo);
		FeedStatus status = newFeedStatus(feed.toString(), feed);
		newPersistedFeed.addFeedStatus(status);
		return newPersistedFeed;
	}

	private PersistedFeed process(Exception e) {
		PersistedFeed newPersistedFeed = new PersistedFeed(persistedFeed.getPodcastId(), persistedFeed.getFeedInfo());
		newPersistedFeed.addFeedError(newFeedError(e));
		log(e);
		return newPersistedFeed;
	}

	private void log(Exception e) {
		if (e instanceof IllegalArgumentException) {
			logger.error("process(Exception) {}", persistedFeed.getFeedId().getUri().toASCIIString(), e);
		} else if (e instanceof NullPointerException) {
			logger.error("process(Exception) {}", persistedFeed.getFeedId().getUri().toASCIIString(), e);
		} else if (e instanceof UnknownHostException) {
			logger.warn("{}: {}", e.getClass().getCanonicalName(), persistedFeed.getFeedId().getUri().toASCIIString());
		} else {
			logger.error("process(Exception) {}", persistedFeed.getFeedId().getUri().toASCIIString(), e);
		}
	}
	
	/**
	 * Create a new {@link FeedInfo} instance based on the instance in this {@link PipelineDownloader} and the given
	 * parsed {@link Feed}
	 * 
	 * @param feed
	 *            a parsed {@link Feed}, not {@code null}
	 * @return a new {@link FeedInfo} instance, not {@code null}
	 */
	private FeedInfo enrich(Feed feed) {

		String name = StringUtils.trimToNull(feed.getTitle());
		if (name == null || name.equals(persistedFeed.getFeedId().getName())) {
			name = persistedFeed.getFeedId().getName();
		}

		FeedId feedId = new FeedId(name, persistedFeed.getFeedId().getUri());

		String language = StringUtils.lowerCase(StringUtils.substring(feed.getLanguage(), 0, 2));
		if (StringUtils.isBlank(language) || language.equals(persistedFeed.getFeedInfo().getLanguage())) {
			language = persistedFeed.getFeedInfo().getLanguage();
		}

		String description = getDescription(feed);
		if (description == null || StringUtils.equals(description, persistedFeed.getFeedInfo().getDescription())) {
			description = persistedFeed.getFeedInfo().getDescription();
		}

		return new FeedInfo(feedId, language, description);

	}

	private String getDescription(Feed feed) {
		
		// first choice : property summary
		String description = StringUtils.trimToNull(feed.getSummary());
		
		// second choice : property description
		if (description == null) {
			description = StringUtils.trimToNull(feed.getDescription() == null ? "" : feed.getDescription().getType());
		}
		
		// third choice : property subtitle
		if (description == null) {
			description = StringUtils.trimToNull(feed.getSubtitle());
		}
		
		return description;

	}
	
	private FeedError newFeedError(Exception e) {
		int podcastId = persistedFeed.getPodcastId();
		LocalDateTime date = LocalDateTime.now();
		String error = e.getClass().getCanonicalName();
		String message = getMessage(e);
		return new FeedError(podcastId, date, error, message);
	}

	private FeedStatus newFeedStatus(String xml, Feed feed) {
		int podcastId = persistedFeed.getPodcastId();
		LocalDateTime date = LocalDateTime.now();
		LocalDate lastUpdate = feed.getLastLocalDate();
		int items = feed.getItems().size();
		int hash = MurmurHash3.hash(feed.toString());
		int bytes = xml.getBytes(StandardCharsets.UTF_8).length;
		return new FeedStatus(podcastId, date, lastUpdate, items, hash, bytes);
	}

	private static String getMessage(Exception e) {
		if (e instanceof HttpResponseException) {
			return "HTTP status " + ((HttpResponseException) e).getStatusCode() + ": " + e.getMessage();
		} else {
			return e.getMessage();
		}
	}
	
}