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
package be.ceau.podcastfinder.store.sqlite.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import be.ceau.podcastfinder.export.ExportableFeed;

/**
 * {@link ResultSetMapper} for {@link ExportableFeed} instances
 */
public class ExportableFeedMapper implements ResultSetMapper<ExportableFeed> {

	public static final ExportableFeedMapper INSTANCE = new ExportableFeedMapper();

	@Override
	public ExportableFeed map(int index, ResultSet r, StatementContext ctx) throws SQLException {
		ExportableFeed feed = new ExportableFeed();
		feed.setDescription(r.getString("description"));
		feed.setLanguage(r.getString("language"));
		feed.setName(r.getString("name"));
		feed.setItems(r.getInt("items"));
		feed.setUpdated(r.getString("lastUpdate"));
		feed.setUri(r.getString("uri"));
		return feed;
	}

}