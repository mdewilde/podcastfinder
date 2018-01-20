package be.ceau.podcastfinder.update.filter;

import java.util.function.Predicate;

import be.ceau.podcastfinder.model.FeedStatus;
import be.ceau.podcastfinder.model.PersistedFeed;

public class UpdateFilter implements Predicate<PersistedFeed> {

	@Override
	public boolean test(PersistedFeed feed) {
		if (!feed.getFeedErrors().isEmpty()) {
			return false;
		}
		if (feed.getFeedInfo().getDescription() == null) {
			return true;
		}
		if (feed.getFeedInfo().getLanguage() == null) {
			return true;
		}
		FeedStatus status = feed.getNewestFeedStatus();
		return status != null && status.getLastUpdate() == null;
	}

}
