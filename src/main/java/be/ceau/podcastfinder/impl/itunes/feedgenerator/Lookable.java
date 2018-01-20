package be.ceau.podcastfinder.impl.itunes.feedgenerator;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.ceau.itunesapi.Lookup;
import be.ceau.itunesapi.response.Response;
import be.ceau.itunesapi.response.Result;
import be.ceau.podcastfinder.model.FeedId;

/**
 * Task encapsulating a single {@link Lookup}.
 */
class Lookable implements Callable<List<FeedId>> {

	private static final Logger logger = LoggerFactory.getLogger(Lookable.class);

	private final Lookup lookup;

	Lookable(Lookup lookup) {
		Objects.requireNonNull(lookup);
		this.lookup = lookup;
	}

	@Override
	public List<FeedId> call() {
		try {
			return lookup.execute().getResults()
					.stream()
					.filter(this::isCompatible)
					.map(this::newPodcast)
					.filter(Objects::nonNull)
					.collect(Collectors.toList());
		} catch (Exception e) {
			logger.warn("FeedGeneratorFinder.Lookable.call() {} {}", e.getClass().getCanonicalName(), e.getMessage());
			return Collections.emptyList();
		}
	}

	/**
	 * @param result
	 *            a {@link Result} as found in a {@link Lookup} {@link Response}
	 * @return {@code true} if the given {@link Result} can be converted to a valid {@link FeedId} instance
	 */
	private boolean isCompatible(Result result) {
		if (result == null || result.getFeedUrl() == null) {
			return false;
		}
		if (result.getCollectionName() == null) {
			logger.warn("isCompatible(Result) url but no title -> {}", result.getFeedUrl());
			return false;
		}
		return true;
	}

	private FeedId newPodcast(Result result) {
		try {
			return new FeedId(result.getCollectionName(), result.getFeedUrl());
		} catch (IllegalArgumentException e) {
			logger.warn("newPodcast(Result) ignoring podcast with invalid URI {}", result.getFeedUrl());
			return null;
		}
	}

}