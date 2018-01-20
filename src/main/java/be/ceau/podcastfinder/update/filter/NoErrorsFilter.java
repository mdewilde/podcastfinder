package be.ceau.podcastfinder.update.filter;

import java.util.function.Predicate;

import be.ceau.podcastfinder.model.PersistedFeed;

public class NoErrorsFilter implements Predicate<PersistedFeed> {

	@Override
	public boolean test(PersistedFeed feed) {
		return feed.getFeedErrors().isEmpty();
	}

}
