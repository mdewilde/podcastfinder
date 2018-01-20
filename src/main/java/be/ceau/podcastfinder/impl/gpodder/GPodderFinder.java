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
package be.ceau.podcastfinder.impl.gpodder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.ceau.gpodder.DirectoryAPI;
import be.ceau.gpodder.Tag;
import be.ceau.podcastfinder.api.Podcastfinder;
import be.ceau.podcastfinder.executor.ExecutorServiceFactory;
import be.ceau.podcastfinder.model.FeedId;

/**
 * {@link Podcastfinder} implementation querying gpodder.net
 */
public class GPodderFinder implements Podcastfinder {

	private static final Logger logger = LoggerFactory.getLogger(GPodderFinder.class);

	private static final int NUM_RESULTS = 10000;

	private final DirectoryAPI api = new DirectoryAPI();

	@Override
	public Stream<FeedId> search() {
		return Stream.concat(getPodcastsFromTags().stream(), getToplistPodcasts().stream());
	}

	@Override
	public void search(Consumer<FeedId> consumer) {
		getPodcastsFromTags().forEach(consumer::accept);
		getToplistPodcasts().forEach(consumer::accept);
	}

	private List<FeedId> getPodcastsFromTags() {
		Set<String> tags = getTags();
		if (tags.isEmpty()) {
			return Collections.emptyList();
		}
		List<Future<List<FeedId>>> futures = new ArrayList<>();
		List<FeedId> podcasts = new ArrayList<>();
		for (String tag : tags) {
			futures.add(ExecutorServiceFactory.submit(new RetrievePodcastsFromTag(tag)));
		}
		for (Future<List<FeedId>> future : futures) {
			try {
				podcasts.addAll(future.get());
			} catch (InterruptedException | ExecutionException e) {
				logger.warn("getPodcastsFromTags() {} : {}", e.getClass().getCanonicalName(), e.getMessage());
			}
		}
		return podcasts;
	}

	private Set<String> getTags() {
		try {
			return api.getTags(NUM_RESULTS)
					.stream()
					.map(Tag::getTag)
					.collect(Collectors.toSet());
		} catch (IOException e) {
			logger.error("getTags()", e);
			return Collections.emptySet();
		}
	}

	private List<FeedId> getToplistPodcasts() {
		try {
			return api.getToplist(NUM_RESULTS)
					.stream()
					.filter(GPodderFinder::isCompatible)
					.map(GPodderFinder::convert)
					.filter(Objects::nonNull)
					.collect(Collectors.toList());
		} catch (IOException e) {
			logger.error("getToplistPodcastStream()", e);
			return Collections.emptyList();
		}
	}

	private static boolean isCompatible(be.ceau.gpodder.Podcast podcast) {
		if (podcast == null || podcast.getUrl() == null) {
			return false;
		}
		if (podcast.getTitle() == null) {
			logger.warn("isCompatible(Podcast) url but no title -> {}", podcast.getUrl());
			return false;
		}
		return true;
	}

	private static FeedId convert(be.ceau.gpodder.Podcast podcast) {
		try {
			return new FeedId(podcast.getTitle(), podcast.getUrl());
		} catch (IllegalArgumentException e) {
			logger.warn("ignoring podcast with invalid URI {}", podcast.getUrl());
			return null;
		}
	}

	private class RetrievePodcastsFromTag implements Callable<List<FeedId>> {

		private final String tag;

		private RetrievePodcastsFromTag(String tag) {
			this.tag = tag;
		}

		@Override
		public List<FeedId> call() throws Exception {
			long start = System.currentTimeMillis();
			try {
				return api.getPodcastsForTag(tag, NUM_RESULTS)
						.stream()
						.filter(GPodderFinder::isCompatible)
						.map(GPodderFinder::convert)
						.filter(Objects::nonNull)
						.collect(Collectors.toList());
			} finally {
				logger.debug("GPodderFinder.RetrievePodcastsFromTag.call() {} millis for tag {}", System.currentTimeMillis() - start, tag);
			}
		}

	}

}
