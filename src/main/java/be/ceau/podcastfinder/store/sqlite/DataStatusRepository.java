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

import java.util.Objects;

import org.skife.jdbi.v2.Handle;

import be.ceau.podcastfinder.model.DataStatus;

/**
 * Class for collecting data into {@link DataStatus} objects
 */
final class DataStatusRepository {

	private static final String SELECT_PODCAST_COUNT = 
			"SELECT count(*) FROM podcasts";

	private static final String SELECT_VALIDATED_PODCAST_COUNT = 
			"SELECT COUNT(*) FROM (SELECT podcastId, items, max(date) FROM status GROUP BY podcastId) WHERE items > 0";		

	private static final String SELECT_ZERO_ITEMS_PODCAST_COUNT = 
			"SELECT COUNT(*) FROM (SELECT podcastId, items, max(date) FROM status GROUP BY podcastId) WHERE items = 0";		

	private static final String SELECT_FAILED_PODCAST_COUNT = 
			"SELECT count(*) FROM (SELECT DISTINCT podcasts.podcastId FROM podcasts	INNER JOIN errors ON podcasts.podcastId = errors.podcastId)";

	private static final String SELECT_UNVERIFIED_PODCAST_COUNT = 
				" SELECT count(*) FROM ("
			+	" 	SELECT DISTINCT podcasts.podcastId "
			+	"	FROM podcasts "
			+	"	LEFT OUTER JOIN status ON podcasts.podcastId = status.podcastId "
			+	"	LEFT OUTER JOIN errors ON podcasts.podcastId = errors.podcastId "
			+	"	WHERE status.podcastId IS NULL AND errors.podcastId IS NULL )";

	private static final String SELECT_ENCLOSURE_COUNT = 
				" SELECT SUM(s1.items) AS total "
			+	" FROM status s1 "
			+	" INNER JOIN ("
			+	"	SELECT podcastId, max(date) AS date"
			+	"	FROM status"
			+	"	GROUP BY podcastId ) s2"
			+	" ON s1.podcastId = s2.podcastId AND s1.date = s2.date";

	private final SQLiteDatabase database;
	
	DataStatusRepository(SQLiteDatabase database) {
		Objects.requireNonNull(database);
		this.database = database;
	}

	int getPodcastCount() {
		return get(SELECT_PODCAST_COUNT);
	}

	int getValidatedPodcastCount() {
		return get(SELECT_VALIDATED_PODCAST_COUNT);
	}

	int getZeroItemsPodcastCount() {
		return get(SELECT_ZERO_ITEMS_PODCAST_COUNT);
	}

	int getFailedPodcastCount() {
		return get(SELECT_FAILED_PODCAST_COUNT);
	}

	int getUnverifiedPodcastCount() {
		return get(SELECT_UNVERIFIED_PODCAST_COUNT);
	}

	int getEnclosureCount() {
		return get(SELECT_ENCLOSURE_COUNT);
	}

	private int get(String sql) {
		Integer ret = database.execute((Handle h) -> h.createQuery(sql)
				.mapTo(Integer.class)
				.first());
		return ret == null ? 0 : ret;
	}

}
