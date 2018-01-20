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
package be.ceau.podcastfinder.store.sqlite.bind;

import org.skife.jdbi.v2.SQLStatement;

import be.ceau.podcastfinder.model.FeedError;
import be.ceau.podcastfinder.util.Timestamp;

public class FeedErrorBinder implements Binder<FeedError> {

	public static final FeedErrorBinder INSTANCE = new FeedErrorBinder();

	@Override
	public void bind(SQLStatement<?> statement, FeedError feederror) {
		statement
				.bind("podcastId", feederror.getPodcastId())
				.bind("date", Timestamp.format(feederror.getDate()))
				.bind("error", feederror.getError())
				.bind("message", feederror.getMessage());
	}

}
