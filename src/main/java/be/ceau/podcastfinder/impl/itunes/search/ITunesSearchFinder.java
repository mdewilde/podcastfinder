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
package be.ceau.podcastfinder.impl.itunes.search;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.ceau.itunesapi.Search;
import be.ceau.itunesapi.request.Country;
import be.ceau.itunesapi.request.Entity;
import be.ceau.itunesapi.request.search.Attribute;
import be.ceau.itunesapi.request.search.Media;
import be.ceau.podcastfinder.api.Podcastfinder;
import be.ceau.podcastfinder.model.FeedId;

/**
 * The iTunes Search API is currently rate-limited to approximately 20 calls per minute.
 */
public class ITunesSearchFinder implements Podcastfinder {

	private static final Logger logger = LoggerFactory.getLogger(ITunesSearchFinder.class);

	private final Country country;
	private final Set<String> searchTerms;

	/**
	 * Constructor
	 * 
	 * @param country
	 *            a {@link Country}, not {@code null}
	 * @param language
	 *            a {@link SearchTerms.Language}, not {@code null}
	 */
	public ITunesSearchFinder(Country country, SearchTerms.Language language) {
		this(country, SearchTerms.get(language));
	}

	/**
	 * Constructor
	 * 
	 * @param country
	 *            a {@link Country}, not {@code null}
	 * @param searchTerms
	 *            a {@link Collection} of {@link String} instances, not {@code null}
	 */
	public ITunesSearchFinder(Country country, Collection<String> searchTerms) {
		Objects.requireNonNull(country);
		Objects.requireNonNull(searchTerms);
		this.country = country;
		this.searchTerms = new LinkedHashSet<>(searchTerms);
	}

	@Override
	public Stream<FeedId> search() {
		return searchTerms.stream()
				.map(word -> rateLimitedSearch(country, word, 3010))
				.flatMap(List::stream);
	}

	@Override
	public void search(Consumer<FeedId> consumer) {
		search().forEach(consumer::accept);
	}

	private List<FeedId> rateLimitedSearch(Country country, String term, int millisBetweenRequests) {
		if (term == null) {
			return Collections.emptyList();
		}
		long start = System.currentTimeMillis();
		List<FeedId> list = search(country, term);
		long remaining = millisBetweenRequests - (System.currentTimeMillis() - start);
		if (remaining > 0) {
			try {
				Thread.sleep(remaining);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		logger.debug("returning {} podcasts for term {}", list.size(), term);
		return list;
	}

	private List<FeedId> search(Country country, String term) {
		return new Search()
				.setMedia(Media.PODCAST)
				.setLimit(200)
				.setEntity(Entity.PODCAST)
				.setAttribute(Attribute.TITLE_TERM)
				.setTerm(term)
				.setCountry(country)
				.execute()
				.getResults()
				.stream()
				.filter(r -> Objects.nonNull(r.getCollectionName()))
				.filter(r -> Objects.nonNull(r.getFeedUrl()))
				.map(r -> {
					try {
						return new FeedId(r.getCollectionName(), r.getFeedUrl());
					} catch (IllegalArgumentException e) {
						logger.warn("ignoring podcast with invalid URI {}", r.getFeedUrl());
						return null;
					}
				})
				.filter(Objects::nonNull)
				.collect(Collectors.toList());
	}

}
