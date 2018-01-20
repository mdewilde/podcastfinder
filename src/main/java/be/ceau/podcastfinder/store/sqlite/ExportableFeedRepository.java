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

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import org.skife.jdbi.v2.Handle;

import be.ceau.podcastfinder.export.ExportableFeed;
import be.ceau.podcastfinder.store.api.ExportableFeedProducer;
import be.ceau.podcastfinder.store.sqlite.mapper.ExportableFeedMapper;

/**
 * Class for interacting with SQLite table {@code `status`}
 */
final class ExportableFeedRepository implements ExportableFeedProducer {

	private static final String SELECT_STATUSES = 
			" SELECT uri, name, language, description, lastUpdate, items "
		+	" FROM podcasts p "
		+	" INNER JOIN ( SELECT podcastId, lastUpdate, items, max(date) AS date FROM status GROUP BY podcastId ) AS s on p.podcastId = s.podcastId "
		+	" WHERE items > 0 "
		+	" ORDER BY language ASC, uri ASC ";

	private static final String SELECT_STATUSES_BY_LANGUAGE = 
			" SELECT uri, name, language, description, lastUpdate, items "
		+	" FROM podcasts p "
		+	" INNER JOIN ( SELECT podcastId, lastUpdate, items, max(date) AS date FROM status GROUP BY podcastId ) AS s on p.podcastId = s.podcastId "
		+	" WHERE items > 0 "
		+	" AND language = :language "
		+	" ORDER BY uri ASC ";
	
	private final SQLiteDatabase database;
	
	ExportableFeedRepository(SQLiteDatabase database) {
		Objects.requireNonNull(database);
		this.database = database;
	}

	@Override
	public List<ExportableFeed> getExportableFeeds() {
		return database.execute((Handle h) -> h.createQuery(SELECT_STATUSES)
				.map(ExportableFeedMapper.INSTANCE)
				.list());
	}

	@Override
	public List<ExportableFeed> getExportableFeeds(String language) {
		return database.execute((Handle h) -> h.createQuery(SELECT_STATUSES_BY_LANGUAGE)
				.bind("language", language)
				.map(ExportableFeedMapper.INSTANCE)
				.list());
	}
	
	@Override
	public void consumeExportableFeeds(Consumer<ExportableFeed> consumer) {
		database.execute((Handle h) -> h.createQuery(SELECT_STATUSES)
				.map(ExportableFeedMapper.INSTANCE)
				.forEach(consumer));
	}

	@Override
	public void consumeExportableFeeds(String language, Consumer<ExportableFeed> consumer) {
		database.execute((Handle h) -> h.createQuery(SELECT_STATUSES_BY_LANGUAGE)
				.bind("language", language)
				.map(ExportableFeedMapper.INSTANCE)
				.forEach(consumer));
	}

}
