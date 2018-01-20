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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import be.ceau.podcastfinder.model.DataStatus;
import be.ceau.podcastfinder.model.Duplicates;
import be.ceau.podcastfinder.model.Duplicates.Dup;

/**
 * Class for collecting data into {@link DataStatus} objects
 */
final class DuplicatesRepository {

	private static final String SELECT_DUPLICATES = 
			" SELECT s1.podcastId AS podcastId1, s2.podcastId AS podcastId2, p1.name as name1, p2.name as name2, s1.items, s1.hash, s1.bytes, p1.uri AS uri1, p2.uri AS uri2, p1.language AS language1, p2.language AS language2, p1.description AS description1, p2.description AS description2 "
		+	" 	FROM ( SELECT podcastId, max(date) AS date, items, hash, bytes FROM status GROUP BY podcastId ) AS s1 "
		+	" 	INNER JOIN ( SELECT podcastId, max(date) AS date, items, hash, bytes FROM status GROUP BY podcastId ) AS s2 "
		+	" 		ON s1.hash = s2.hash and s1.items = s2.items and s1.bytes = s2.bytes and s1.podcastId < s2.podcastId "
		+	" 	INNER JOIN podcasts p1 ON s1.podcastId = p1.podcastId "
		+	" 	INNER JOIN podcasts p2 ON s2.podcastId = p2.podcastId "
		+	" WHERE s1.items > 0 ";

	private final SQLiteDatabase database;
	
	DuplicatesRepository(SQLiteDatabase database) {
		Objects.requireNonNull(database);
		this.database = database;
	}

	Set<Duplicates> getDuplicates() {
		
		final Map<Integer, Duplicates> duplicatesMap = new TreeMap<>();
		
		database.execute((Handle h) -> h.createQuery(SELECT_DUPLICATES)
				.map(new ResultSetMapper<Duplicates>() {

					@Override
					public Duplicates map(int index, ResultSet r, StatementContext ctx) throws SQLException {
						
						Duplicates.Dup dup1 = new Duplicates.Dup();
						dup1.setPodcastId(r.getInt("podcastId1"));
						String uri1 = StringUtils.removeEnd(r.getString("uri1").trim().toLowerCase(Locale.ENGLISH), "/");
						dup1.setUri(URI.create(uri1));
						dup1.setName(r.getString("name1"));
						dup1.setLanguage(r.getString("language1"));
						dup1.setDescription(r.getString("description1"));

						Duplicates.Dup dup2 = new Duplicates.Dup();
						dup2.setPodcastId(r.getInt("podcastId2"));
						String uri2 = StringUtils.removeEnd(r.getString("uri2").trim().toLowerCase(Locale.ENGLISH), "/");
						dup2.setUri(URI.create(uri2));
						dup2.setName(r.getString("name2"));
						dup2.setLanguage(r.getString("language2"));
						dup2.setDescription(r.getString("description2"));

						Duplicates duplicates = new Duplicates(r.getInt("items"), r.getInt("hash"), r.getInt("bytes"), Arrays.asList(dup1, dup2));

						return duplicates;
					}
					
				})
				.forEach(duplicates -> {
					
					// each dup needs to belong to only one duplicates instance
					// if such an instance already exists, add all to that
					
					Duplicates existingDuplicates = duplicates.getDups().stream()
						.map(Dup::getPodcastId)
						.map(duplicatesMap::get)
						.filter(Objects::nonNull)
						.findFirst()
						.orElse(null);
					
					if (existingDuplicates != null) {
						existingDuplicates.addAll(duplicates.getDups());
						duplicates.getDups().stream()
								.map(Dup::getPodcastId)
								.forEach(id -> duplicatesMap.put(id, existingDuplicates));
					} else {
						duplicates.getDups().stream()
								.map(Dup::getPodcastId)
								.forEach(id -> duplicatesMap.put(id, duplicates));
					}
					
				}));
		
		return new HashSet<>(duplicatesMap.values());
		
	}

}
