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
import be.ceau.podcastfinder.model.FeedInfo;

/**
 * {@link ResultSetMapper} for {@link FeedInfo} instances
 */
public class FeedInfoMapper implements ResultSetMapper<FeedInfo> {

	public static final FeedInfoMapper INSTANCE = new FeedInfoMapper();

	@Override
	public FeedInfo map(int index, ResultSet r, StatementContext ctx) throws SQLException {
		FeedId feedId = FeedIdMapper.INSTANCE.map(index, r, ctx);
		String language = r.getString("language");
		String description = r.getString("description");
		return new FeedInfo(feedId, language, description);
	}

}
