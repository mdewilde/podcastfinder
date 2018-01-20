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
package be.ceau.podcastfinder.store.sqlite;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.skife.jdbi.v2.Handle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.ceau.podcastfinder.export.ExportableFeed;
import be.ceau.podcastfinder.model.DataStatus;
import be.ceau.podcastfinder.model.Duplicates;
import be.ceau.podcastfinder.model.FeedError;
import be.ceau.podcastfinder.model.FeedId;
import be.ceau.podcastfinder.model.FeedInfo;
import be.ceau.podcastfinder.model.FeedStatus;
import be.ceau.podcastfinder.model.PersistedFeed;
import be.ceau.podcastfinder.store.api.ExportableFeedProducer;
import be.ceau.podcastfinder.store.api.PersistedFeedProducer;

/**
 * Data persistence entry point based around a local SQLite file.
 */
public class PodcastFinderStore implements AutoCloseable, PersistedFeedProducer, ExportableFeedProducer {

	private static final Logger logger = LoggerFactory.getLogger(PodcastFinderStore.class);

	private final AtomicBoolean closed = new AtomicBoolean(false);

	private final SQLiteDatabase database;
	private final FeedIdRepository feedIdRepository;
	private final FeedInfoRepository feedInfoRepository;
	private final FeedStatusRepository statusRepository;
	private final FeedErrorsRepository errorsRepository;
	private final DataStatusRepository dataStatusRepository;
	private final DuplicatesRepository duplicatesRepository;
	private final ExportableFeedRepository exportableFeedRepository;

	public PodcastFinderStore() {
		this.database = SQLiteDatabase.getInstance();
		this.feedIdRepository = new FeedIdRepository(database);
		this.feedInfoRepository = new FeedInfoRepository(database);
		this.statusRepository = new FeedStatusRepository(database);
		this.errorsRepository = new FeedErrorsRepository(database);
		this.dataStatusRepository = new DataStatusRepository(database);
		this.duplicatesRepository = new DuplicatesRepository(database);
		this.exportableFeedRepository = new ExportableFeedRepository(database);
	}

	public void add(FeedId feedId) {
		requireOpen();
		feedIdRepository.add(feedId);
	}

	public void addFoundFeeds(Collection<FeedId> feedIds) {
		requireOpen();
		feedIdRepository.addAll(feedIds);
	}

	public void add(FeedInfo feedInfo) {
		requireOpen();
		feedInfoRepository.add(feedInfo);
	}

	public void addFeedInfos(Collection<FeedInfo> infos) {
		requireOpen();
		feedInfoRepository.addAll(infos);
	}

	public void update(int podcastId, FeedInfo feedInfo) {
		requireOpen();
		feedInfoRepository.update(podcastId, feedInfo);
	}

	public void add(FeedStatus feedStatus) {
		requireOpen();
		statusRepository.add(feedStatus);
	}

	public void add(FeedError feedError) {
		requireOpen();
		errorsRepository.add(feedError);
	}

	public void addFeedStatuses(Collection<FeedStatus> feedStatuses) {
		requireOpen();
		statusRepository.addAll(feedStatuses);
	}

	public void addFeedErrors(Collection<FeedError> feedErrors) {
		requireOpen();
		errorsRepository.addAll(feedErrors);
	}

	public List<PersistedFeed> get() {
		requireOpen();
		final Map<Integer, PersistedFeed> feedMap = feedInfoRepository.get()
				.stream()
				.collect(Collectors.toMap(PersistedFeed::getPodcastId, Function.identity()));

		statusRepository.get()
				.forEach(s -> {
					PersistedFeed persistedFeed = feedMap.get(s.getPodcastId());
					if (persistedFeed != null) {
						persistedFeed.addFeedStatus(s);
					} else {
						logger.warn("could not retrieve PersistedFeed instance for podcastId {}", s.getPodcastId());
					}
				});

		errorsRepository.get()
			.forEach(s -> {
				PersistedFeed persistedFeed = feedMap.get(s.getPodcastId());
				if (persistedFeed != null) {
					persistedFeed.addFeedError(s);
				} else {
					logger.warn("could not retrieve PersistedFeed instance for podcastId {}", s.getPodcastId());
				}
			});

		return new ArrayList<>(feedMap.values());
	}

	public List<PersistedFeed> getPersistedFeedsWithoutStatus() {
		requireOpen();
		return feedInfoRepository.get();
	}

	public PersistedFeed get(int podcastId) {
		requireOpen();
		final PersistedFeed feed = feedInfoRepository.get(podcastId);
		if (feed != null) {
			statusRepository.get(podcastId).forEach(feed::addFeedStatus);
			errorsRepository.get(podcastId).forEach(feed::addFeedError);
		}
		return feed;
	}

	public PersistedFeed get(URI uri) {
		requireOpen();
		Integer podcastId = getPodcastId(uri);
		if (podcastId != null) {
			return get(podcastId);
		}
		return null;
	}

	public Integer getPodcastId(URI uri) {
		requireOpen();
		return feedInfoRepository.getPodcastId(uri);
	}

	public int getMaxPodcastId() {
		requireOpen();
		return feedInfoRepository.getMaxPodcastId();
	}

	public List<PersistedFeed> getPersistedFeeds(int minPodcastId, int maxPodcastId) {
		requireOpen();

		final List<PersistedFeed> feeds = feedInfoRepository.getRange(minPodcastId, maxPodcastId);
		
		final Map<Integer, PersistedFeed> feedMap = feeds.stream()
				.collect(Collectors.toMap(PersistedFeed::getPodcastId, Function.identity()));

		statusRepository.getRange(minPodcastId, maxPodcastId)
				.forEach(status -> {
					PersistedFeed persistedFeed = feedMap.get(status.getPodcastId());
					if (persistedFeed != null) {
						persistedFeed.addFeedStatus(status);
					} else {
						logger.warn("no matching PersistedFeed instance for podcastId {} found in table `status`", status.getPodcastId());
					}
				});
		
		errorsRepository.getRange(minPodcastId, maxPodcastId)
			.forEach(error -> {
				PersistedFeed persistedFeed = feedMap.get(error.getPodcastId());
				if (persistedFeed != null) {
					persistedFeed.addFeedError(error);
				} else {
					logger.warn("no matching PersistedFeed instance for podcastId {} found in table `errors`", error.getPodcastId());
				}
			});

		return feeds;
	}

	public DataStatus getDataStatus() {
		DataStatus dataStatus = new DataStatus();
		dataStatus.setTotal(dataStatusRepository.getPodcastCount());
		dataStatus.setValidated(dataStatusRepository.getValidatedPodcastCount());
		dataStatus.setZeroItems(dataStatusRepository.getZeroItemsPodcastCount());
		dataStatus.setFailed(dataStatusRepository.getFailedPodcastCount());
		dataStatus.setUnverified(dataStatusRepository.getUnverifiedPodcastCount());
		dataStatus.setEnclosures(dataStatusRepository.getEnclosureCount());
		return dataStatus;
	}
	
	public Set<Duplicates> getDuplicates() {
		return duplicatesRepository.getDuplicates();
	}

	private void requireOpen() {
		if (closed.get()) {
			throw new IllegalStateException("This PodcastFinderStore instance has been closed");
		}
	}
	
	public void clean() {
		errorsRepository.deleteAllButNewest();
		database.execute((Handle h) -> h.createStatement("vacuum;").execute());
	}
	
	@Override
	public void close() {
		if (closed.compareAndSet(false, true)) {
			database.close();
		}
	}

	@Override
	public List<ExportableFeed> getExportableFeeds() {
		return exportableFeedRepository.getExportableFeeds();
	}

	@Override
	public List<ExportableFeed> getExportableFeeds(String language) {
		return exportableFeedRepository.getExportableFeeds(language);
	}

	@Override
	public void consumeExportableFeeds(Consumer<ExportableFeed> consumer) {
		exportableFeedRepository.consumeExportableFeeds(consumer);
	}
	
	@Override
	public void consumeExportableFeeds(String language, Consumer<ExportableFeed> consumer) {
		exportableFeedRepository.consumeExportableFeeds(language, consumer);
	}

	public Set<String> getLanguages() {
		return feedInfoRepository.getLanguages();
	}
	
	public void deleteLanguage(String language) {
		feedInfoRepository.deleteLanguage(language);
	}

	public void delete(final int podcastId) {
		statusRepository.delete(podcastId);
		errorsRepository.delete(podcastId);
		feedIdRepository.delete(podcastId);
	}
	
}
