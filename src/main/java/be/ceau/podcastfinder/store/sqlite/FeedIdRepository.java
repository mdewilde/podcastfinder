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

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.PreparedBatch;
import org.skife.jdbi.v2.Update;

import be.ceau.podcastfinder.model.FeedId;
import be.ceau.podcastfinder.store.sqlite.bind.FeedIdBinder;
import be.ceau.podcastfinder.store.sqlite.mapper.FeedIdMapper;

/**
 * Class for interacting with SQLite table {@code `podcasts`}
 */
final class FeedIdRepository {

	private static final String INSERT_PODCAST = 
			"INSERT OR IGNORE INTO podcasts (name, uri, insertDate) VALUES (:name, :uri, :insertDate)";

	private static final String SELECT_PODCASTS = 
			"SELECT name, uri FROM podcasts";

	private static final String SELECT_PODCAST_BY_ID = 
			"SELECT name, uri FROM podcasts WHERE podcastId = :podcastId";

	private static final String SELECT_PODCASTS_BY_ID_RANGE = 
			"SELECT name, uri FROM podcasts WHERE podcastId >= :minPodcastId and podcastId <= :maxPodcastId";

	private static final String DELETE_BY_PODCAST_ID = 
			"DELETE FROM podcasts WHERE podcastId = :podcastId";

	private final SQLiteDatabase database;
	
	FeedIdRepository(SQLiteDatabase database) {
		Objects.requireNonNull(database);
		this.database = database;
	}

	void add(FeedId feedId) {
		database.execute((Handle h) -> { 
			Update update = h.createStatement(INSERT_PODCAST);
			FeedIdBinder.INSTANCE.bind(update, feedId);
			update.execute();
		});
	}

	void addAll(Collection<FeedId> feedIds) {
		database.execute((Handle h) -> { 
			PreparedBatch batch = h.prepareBatch(INSERT_PODCAST);
			feedIds.forEach(feedId -> FeedIdBinder.INSTANCE.bind(batch.add(), feedId));
			batch.execute();
		});
	}

	List<FeedId> get() {
		return database.execute((Handle h) -> h.createQuery(SELECT_PODCASTS)
				.map(FeedIdMapper.INSTANCE)
				.list());
	}

	FeedId get(final int podcastId) {
		return database.execute((Handle h) -> h.createQuery(SELECT_PODCAST_BY_ID)
				.bind("podcastId", podcastId)
				.map(FeedIdMapper.INSTANCE)
				.first());
	}

	List<FeedId> getRange(final int minPodcastId, final int maxPodcastId) {
		return database.execute((Handle h) -> h.createQuery(SELECT_PODCASTS_BY_ID_RANGE)
				.bind("minPodcastId", minPodcastId)
				.bind("maxPodcastId", maxPodcastId)
				.map(FeedIdMapper.INSTANCE)
				.list());
	}

	boolean delete(final int podcastId) {
		return database.execute((Handle h) -> h.createStatement(DELETE_BY_PODCAST_ID)
				.bind("podcastId", podcastId)
				.execute() > 0);
	}

}
