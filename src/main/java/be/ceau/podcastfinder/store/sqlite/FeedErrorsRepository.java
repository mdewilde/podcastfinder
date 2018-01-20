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

import be.ceau.podcastfinder.model.FeedError;
import be.ceau.podcastfinder.store.sqlite.bind.FeedErrorBinder;
import be.ceau.podcastfinder.store.sqlite.mapper.FeedErrorMapper;

/**
 * Class for interacting with SQLite table {@code `errors`}
 */
final class FeedErrorsRepository {

	private static final String INSERT_ERROR = 
			"INSERT INTO errors (podcastId, date, error, message) VALUES (:podcastId, :date, :error, :message)";

	private static final String SELECT_ERRORS = 
			"SELECT podcastId, date, error, message FROM errors";

	private static final String SELECT_ERRORS_BY_ID = 
			"SELECT podcastId, date, error, message FROM errors WHERE podcastId = :podcastId";

	private static final String SELECT_ERRORS_BY_ID_RANGE = 
			"SELECT podcastId, date, error, message FROM errors WHERE podcastId >= :minPodcastId and podcastId <= :maxPodcastId";

	private static final String DELETE_ALL_BUT_NEWEST = 
			"DELETE FROM errors WHERE ROWID NOT IN (SELECT ROWID FROM (SELECT ROWID, podcastId, max(date) FROM errors GROUP BY podcastId))";

	private static final String DELETE_BY_PODCAST_ID = 
			"DELETE FROM errors WHERE podcastId = :podcastId";

	private final SQLiteDatabase database;
	
	FeedErrorsRepository(SQLiteDatabase database) {
		Objects.requireNonNull(database);
		this.database = database;
	}

	void add(FeedError feedError) {
		database.execute((Handle h) -> { 
			Update update = h.createStatement(INSERT_ERROR);
			FeedErrorBinder.INSTANCE.bind(update, feedError);
			update.execute();
		});
	}

	void addAll(Collection<FeedError> feedErrors) {
		database.execute((Handle h) -> { 
			PreparedBatch batch = h.prepareBatch(INSERT_ERROR);
			feedErrors.forEach(feedError -> FeedErrorBinder.INSTANCE.bind(batch.add(), feedError));
			batch.execute();
		});
	}

	List<FeedError> get() {
		return database.execute((Handle h) -> h.createQuery(SELECT_ERRORS)
				.map(FeedErrorMapper.INSTANCE)
				.list());
	}

	List<FeedError> get(final int podcastId) {
		return database.execute((Handle h) -> h.createQuery(SELECT_ERRORS_BY_ID)
				.bind("podcastId", podcastId)
				.map(FeedErrorMapper.INSTANCE)
				.list());
	}

	List<FeedError> getRange(final int minPodcastId, final int maxPodcastId) {
		return database.execute((Handle h) -> h.createQuery(SELECT_ERRORS_BY_ID_RANGE)
				.bind("minPodcastId", minPodcastId)
				.bind("maxPodcastId", maxPodcastId)
				.map(FeedErrorMapper.INSTANCE)
				.list());
	}

	void deleteAllButNewest() {
		database.execute((Handle h) -> h.createStatement(DELETE_ALL_BUT_NEWEST).execute());
	}

	boolean delete(final int podcastId) {
		return database.execute((Handle h) -> h.createStatement(DELETE_BY_PODCAST_ID)
				.bind("podcastId", podcastId)
				.execute() > 0);
	}

}
