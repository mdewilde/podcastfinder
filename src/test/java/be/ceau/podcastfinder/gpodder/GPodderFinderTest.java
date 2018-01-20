package be.ceau.podcastfinder.gpodder;

import org.junit.Test;

import be.ceau.podcastfinder.impl.gpodder.GPodderFinder;

public class GPodderFinderTest {

	@Test
	public void streamTest() {
		new GPodderFinder().search().forEach(System.out::println);
	}
	
}
