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

import be.ceau.podcastfinder.model.FeedStatus;
import be.ceau.podcastfinder.store.sqlite.bind.FeedStatusBinder;
import be.ceau.podcastfinder.store.sqlite.mapper.FeedStatusMapper;

/**
 * Class for interacting with SQLite table {@code `status`}
 */
final class FeedStatusRepository {

	private static final String INSERT_STATUS = 
			"INSERT INTO status (podcastId, date, lastUpdate, items, hash, bytes) VALUES (:podcastId, :date, :lastUpdate, :items, :hash, :bytes)";

	private static final String SELECT_STATUSES = 
			"SELECT podcastId, date, lastUpdate, items, hash, bytes FROM status";

	private static final String SELECT_STATUSES_BY_ID = 
			"SELECT podcastId, date, lastUpdate, items, hash, bytes FROM status WHERE podcastId = :podcastId";

	private static final String SELECT_STATUSES_BY_ID_RANGE = 
			"SELECT podcastId, date, lastUpdate, items, hash, bytes FROM status WHERE podcastId >= :minPodcastId and podcastId <= :maxPodcastId";

	private static final String DELETE_BY_PODCAST_ID = 
			"DELETE FROM status WHERE podcastId = :podcastId";

	private final SQLiteDatabase database;
	
	FeedStatusRepository(SQLiteDatabase database) {
		Objects.requireNonNull(database);
		this.database = database;
	}

	void add(FeedStatus feedStatus) {
		database.execute((Handle h) -> { 
			Update update = h.createStatement(INSERT_STATUS);
			FeedStatusBinder.INSTANCE.bind(update, feedStatus);
			update.execute();
		});
	}

	void addAll(Collection<FeedStatus> feedStatuses) {
		database.execute((Handle h) -> { 
			PreparedBatch batch = h.prepareBatch(INSERT_STATUS);
			feedStatuses.forEach(feedStatus -> FeedStatusBinder.INSTANCE.bind(batch.add(), feedStatus));
			batch.execute();
		});
	}

	List<FeedStatus> get() {
		return database.execute((Handle h) -> h.createQuery(SELECT_STATUSES)
				.map(FeedStatusMapper.INSTANCE)
				.list());
	}

	List<FeedStatus> get(final int podcastId) {
		return database.execute((Handle h) -> h.createQuery(SELECT_STATUSES_BY_ID)
				.bind("podcastId", podcastId)
				.map(FeedStatusMapper.INSTANCE)
				.list());
	}

	List<FeedStatus> getRange(final int minPodcastId, final int maxPodcastId) {
		return database.execute((Handle h) -> h.createQuery(SELECT_STATUSES_BY_ID_RANGE)
				.bind("minPodcastId", minPodcastId)
				.bind("maxPodcastId", maxPodcastId)
				.map(FeedStatusMapper.INSTANCE)
				.list());
	}

	boolean delete(final int podcastId) {
		return database.execute((Handle h) -> h.createStatement(DELETE_BY_PODCAST_ID)
				.bind("podcastId", podcastId)
				.execute() > 0);
	}

}
