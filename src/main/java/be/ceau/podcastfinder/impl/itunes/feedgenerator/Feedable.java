package be.ceau.podcastfinder.impl.itunes.feedgenerator;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.ceau.itunesapi.FeedGenerator;
import be.ceau.itunesapi.request.Country;
import be.ceau.itunesapi.request.feedgenerator.FeedFormat;
import be.ceau.itunesapi.request.feedgenerator.FeedType;
import be.ceau.itunesapi.request.feedgenerator.MediaType;

/**
 * Single task to retrieve all Apple IDs of the top podcasts in a certain country.
 */
class Feedable implements Callable<Set<String>> {

	private static final Logger logger = LoggerFactory.getLogger(Feedable.class);

	private final FeedGenerator feedGenerator;

	Feedable(Country country) {
		Objects.requireNonNull(country);
		this.feedGenerator = new FeedGenerator()
				.setMediaType(MediaType.PODCASTS)
				.setFeedType(FeedType.TOP_PODCASTS)
				.setFormat(FeedFormat.JSON)
				.setResultsLimit(200)
				.setAllowExplicit(true)
				.setCountry(country);
	}

	@Override
	public Set<String> call() {
		try {
			return feedGenerator.execute().getResults()
					.stream()
					.map(r -> r.getId())
					.collect(Collectors.toSet());
		} catch (Exception e) {
			logger.warn("Feedable.call() {} {}", e.getClass().getCanonicalName(), e.getMessage());
			return Collections.emptySet();
		}
	}
}