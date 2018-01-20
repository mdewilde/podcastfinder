package be.ceau.podcastfinder.itunes;

import org.junit.Test;

import be.ceau.podcastfinder.impl.itunes.feedgenerator.FeedGeneratorFinder;

public class FeedGeneratorFinderTest {

	@Test
	public void stream() {
		new FeedGeneratorFinder().search().forEach(System.out::println);
	}

}
