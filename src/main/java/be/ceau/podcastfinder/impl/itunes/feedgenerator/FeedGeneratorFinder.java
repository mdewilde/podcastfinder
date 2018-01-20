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
package be.ceau.podcastfinder.impl.itunes.feedgenerator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.ceau.itunesapi.Lookup;
import be.ceau.itunesapi.request.Country;
import be.ceau.podcastfinder.api.Podcastfinder;
import be.ceau.podcastfinder.executor.ExecutorServiceFactory;
import be.ceau.podcastfinder.model.FeedId;
import be.ceau.podcastparser.util.UnmodifiableSet;

/**
 * {@link Podcastfinder} implementation that queries Apple Podcasts using the feed generator API.
 */
public class FeedGeneratorFinder implements Podcastfinder {

	private static final Logger logger = LoggerFactory.getLogger(FeedGeneratorFinder.class);

	private static final int IDS_PER_LOOKUP = 150;

	// as of 2018-01-01, calls for these countries do not return an answer at all
	private static final Set<String> ISOS_TO_IGNORE = UnmodifiableSet.of("AD", "AF", "AQ", "AS", "AW", "AX", "BA", "BD",
			"BI", "BL", "BQ", "BV", "CC", "CD", "CF", "CI", "CK", "CM", "CU", "CW", "CX", "DJ", "EH", "ER", "ET", "FK",
			"FO", "GA", "GE", "GF", "GG", "GI", "GL", "GN", "GP", "GQ", "GS", "GU", "HM", "HT", "IM", "IO", "IQ", "IR",
			"JE", "KI", "KM", "KP", "LI", "LS", "LY", "MA", "MC", "ME", "MF", "MH", "MM", "MP", "MQ", "MV", "NC", "NR",
			"NU", "PF", "PM", "PN", "PR", "PS", "RE", "RS", "RW", "SD", "SH", "SJ", "SM", "SO", "SS", "SX", "SY", "TF",
			"TG", "TK", "TL", "TO", "TV", "TZ", "UM", "VA", "VI", "VU", "WF", "WS", "YT", "ZM");

	@Override
	public Stream<FeedId> search() {
		Set<String> ids = getIds();
		List<Lookup> lookups = getLookups(ids);
		List<FeedId> podcasts = getPodcasts(lookups);
		return podcasts.stream();
	}

	@Override
	public void search(Consumer<FeedId> consumer) {
		search().forEach(consumer::accept);
	}

	/**
	 * @return a {@link Set} of {@link String} ids of podcasts in Apple iTunes
	 */
	private Set<String> getIds() {
		List<Future<Set<String>>> futures = Country.ALL.stream()
				.filter(c -> !ISOS_TO_IGNORE.contains(c.getIso()))
				.map(Feedable::new)
				.map(ExecutorServiceFactory::submit)
				.collect(Collectors.toList());
		
		return futures.stream()
				.map(this::getIdSet)
				.flatMap(Set::stream)
				.collect(Collectors.toSet());
	}

	private Set<String> getIdSet(Future<Set<String>> future) {
		try {
			return future.get();
		} catch (InterruptedException | ExecutionException e) {
			logger.error("getIdSet(Future<Set<String>>) {} : {}", e.getClass().getCanonicalName(), e.getMessage());
			return Collections.<String>emptySet();
		}
	}

	private List<Lookup> getLookups(Set<String> ids) {
		List<Lookup> lookups = new ArrayList<>();
		Iterator<String> iterator = ids.iterator();
		Lookup lookup = new Lookup();
		while (iterator.hasNext()) {
			lookup.addId(iterator.next());
			if (lookup.getIds().size() > IDS_PER_LOOKUP) {
				lookups.add(lookup);
				lookup = new Lookup();
			}
		}
		if (lookup.getIds().size() > 0) {
			lookups.add(lookup);
		}
		return lookups;
	}

	/**
	 * Accept a collection of {@link Lookup} instances, execute each one, convert results and aggregate into {@link FeedId}
	 * instances
	 * 
	 * @param lookups
	 *            {@link Collection} of {@link Lookup{ instances
	 * @return a {@link List} of {@link FeedId} instances
	 */
	private List<FeedId> getPodcasts(Collection<Lookup> lookups) {
		List<Future<List<FeedId>>> futures = new ArrayList<>();
		for (Lookup lookup : lookups) {
			futures.add(ExecutorServiceFactory.submit(new Lookable(lookup)));
		}
		List<FeedId> podcasts = futures.stream()
				.map(this::getPodcastList)
				.flatMap(List::stream)
				.collect(Collectors.toList());
		return podcasts;
	}

	private List<FeedId> getPodcastList(Future<List<FeedId>> future) {
		try {
			return future.get();
		} catch (InterruptedException | ExecutionException e) {
			logger.error("getPodcastList(Future<List<Podcast>>) {} : {}", e.getClass().getCanonicalName(), e.getMessage());
			return Collections.emptyList();
		}
	}

}
