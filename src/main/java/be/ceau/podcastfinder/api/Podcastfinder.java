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
package be.ceau.podcastfinder.api;

import java.util.function.Consumer;
import java.util.stream.Stream;

import be.ceau.podcastfinder.model.FeedId;

/**
 * Implementations contain a strategy for retrieving parsed {@link FeedId} instances from a specific source.
 */
public interface Podcastfinder {

	/**
	 * Retrieves podcasts using the strategy in this implementation, and returns as a stream. Stream may be lazy or eager,
	 * depending on implementation internals.
	 * 
	 * @return a {@link Stream} of {@link FeedId} instances, never {@code null}
	 */
	public Stream<FeedId> search();

	/**
	 * Retrieves podcasts using the strategy in this implementation, and offers each to the provided consumer.
	 * 
	 * @param consumer
	 *            a {@link Consumer} of {@link FeedId} instances, can not be {@code null}
	 */
	public void search(Consumer<FeedId> consumer);

}
