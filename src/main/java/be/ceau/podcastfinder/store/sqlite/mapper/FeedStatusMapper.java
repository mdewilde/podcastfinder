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
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import be.ceau.podcastfinder.model.FeedStatus;
import be.ceau.podcastfinder.util.Timestamp;

/**
 * {@link ResultSetMapper} for {@link FeedStatus} instances
 */
public class FeedStatusMapper implements ResultSetMapper<FeedStatus> {

	public static final FeedStatusMapper INSTANCE = new FeedStatusMapper();

	@Override
	public FeedStatus map(int index, ResultSet r, StatementContext ctx) throws SQLException {
		int podcastId = r.getInt("podcastId");
		LocalDateTime date = Timestamp.parseLocalDateTime(r.getString("date"));
		LocalDate lastUpdate = Timestamp.parseLocalDate(r.getString("lastUpdate"));
		int items = r.getInt("items");
		int hash = r.getInt("hash");
		int bytes = r.getInt("bytes");
		return new FeedStatus(podcastId, date, lastUpdate, items, hash, bytes);
	}

}
