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
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.PreparedBatch;
import org.skife.jdbi.v2.Update;

import be.ceau.podcastfinder.model.FeedInfo;
import be.ceau.podcastfinder.model.PersistedFeed;
import be.ceau.podcastfinder.store.sqlite.bind.FeedInfoBinder;
import be.ceau.podcastfinder.store.sqlite.mapper.PersistedFeedMapper;

/**
 * Class for interacting with SQLite table {@code `podcasts`}
 */
final class FeedInfoRepository {

	private static final String INSERT_PODCAST =
			"INSERT INTO podcasts (name, uri, language, description, insertDate) VALUES (:name, :uri, :language, :description, :insertDate)";

	private static final String UPDATE_PODCAST = 
			"UPDATE podcasts SET name = :name, language = :language, description = :description WHERE podcastId = :podcastId";

	private static final String SELECT_PODCASTS = 
			"SELECT podcastId, name, uri, language, description, insertDate FROM podcasts";

	private static final String SELECT_PODCAST_ID_BY_URI = 
			"SELECT podcastId FROM podcasts WHERE podcastId = :podcastId";

	private static final String SELECT_PODCAST_BY_ID = 
			"SELECT podcastId, name, uri, language, description, insertDate FROM podcasts WHERE podcastId = :podcastId";

	private static final String SELECT_PODCASTS_BY_ID_RANGE = 
			"SELECT podcastId, name, uri, language, description, insertDate FROM podcasts WHERE podcastId >= :minPodcastId and podcastId <= :maxPodcastId";

	private static final String SELECT_MAX_PODCAST_ID = 
			"SELECT MAX(podcastId) FROM podcasts";

	private static final String SELECT_LANGUAGES = 
			"SELECT DISTINCT language FROM podcasts ORDER BY language ASC";

	private static final String SET_LANGUAGE_TO_NULL = 
			"UPDATE podcasts SET language = NULL WHERE language = :language";

	private final SQLiteDatabase database;
	
	FeedInfoRepository(SQLiteDatabase database) {
		Objects.requireNonNull(database);
		this.database = database;
	}

	void add(FeedInfo feedInfo) {
		database.execute((Handle h) -> { 
			Update update = h.createStatement(INSERT_PODCAST);
			FeedInfoBinder.INSTANCE.bind(update, feedInfo);
			update.execute();
		});
	}

	void addAll(Collection<FeedInfo> feedIds) {
		database.execute((Handle h) -> { 
			PreparedBatch batch = h.prepareBatch(INSERT_PODCAST);
			feedIds.forEach(feedInfo -> FeedInfoBinder.INSTANCE.bind(batch.add(), feedInfo));
			batch.execute();
		});
	}

	List<PersistedFeed> get() {
		return database.execute((Handle h) -> h.createQuery(SELECT_PODCASTS)
				.map(PersistedFeedMapper.INSTANCE)
				.list());
	}

	PersistedFeed get(final int podcastId) {
		return database.execute((Handle h) -> h.createQuery(SELECT_PODCAST_BY_ID)
				.bind("podcastId", podcastId)
				.map(PersistedFeedMapper.INSTANCE)
				.first());
	}

	List<PersistedFeed> getRange(final int minPodcastId, final int maxPodcastId) {
		return database.execute((Handle h) -> h.createQuery(SELECT_PODCASTS_BY_ID_RANGE)
				.bind("minPodcastId", minPodcastId)
				.bind("maxPodcastId", maxPodcastId)
				.map(PersistedFeedMapper.INSTANCE)
				.list());
	}

	void update(int podcastId, FeedInfo feedInfo) {
		database.execute((Handle h) -> {
			h.createStatement(UPDATE_PODCAST)
					.bind("podcastId", podcastId)
					.bind("name", feedInfo.getFeedId().getName())
					.bind("language", feedInfo.getLanguage())
					.bind("description", feedInfo.getDescription())
					.execute();
		});
	}

	Integer getPodcastId(URI uri) {
		return database.execute((Handle h) -> h.createQuery(SELECT_PODCAST_ID_BY_URI)
				.bind("uri", uri.toASCIIString())
				.mapTo(Integer.class)
				.first());
	}

	int getMaxPodcastId() {
		return database.execute((Handle h) -> h.createQuery(SELECT_MAX_PODCAST_ID)
				.mapTo(Integer.class)
				.first());
	}

	Set<String> getLanguages() {
		return database.execute((Handle h) -> h.createQuery(SELECT_LANGUAGES)
				.mapTo(String.class)
				.list())
				.stream()
				.filter(Objects::nonNull)
				.collect(Collectors.toCollection(TreeSet::new));
	}

	void deleteLanguage(String language) {
		database.execute((Handle h) -> h.createStatement(SET_LANGUAGE_TO_NULL)
				.bind("language", language)
				.execute());
	}

}
