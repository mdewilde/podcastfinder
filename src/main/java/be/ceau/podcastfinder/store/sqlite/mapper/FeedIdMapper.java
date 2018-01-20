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

import be.ceau.podcastfinder.model.FeedId;

/**
 * {@link ResultSetMapper} for {@link FeedId} instances
 */
public class FeedIdMapper implements ResultSetMapper<FeedId> {

	public static final FeedIdMapper INSTANCE = new FeedIdMapper();

	@Override
	public FeedId map(int index, ResultSet r, StatementContext ctx) throws SQLException {
		return new FeedId(r.getString("name"), r.getString("uri"));
	}

}
