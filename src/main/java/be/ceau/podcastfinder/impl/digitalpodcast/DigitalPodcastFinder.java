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
package be.ceau.podcastfinder.impl.digitalpodcast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.ceau.digitalpodcast.DigitalPodcastAPI;
import be.ceau.podcastfinder.api.Podcastfinder;
import be.ceau.podcastfinder.executor.ExecutorServiceFactory;
import be.ceau.podcastfinder.model.FeedId;

/**
 * {@link Podcastfinder} implementation querying digitalpodcast.com
 */
public class DigitalPodcastFinder implements Podcastfinder {

	private static final Logger logger = LoggerFactory.getLogger(DigitalPodcastFinder.class);

	private final DigitalPodcastAPI api = new DigitalPodcastAPI();

	@Override
	public Stream<FeedId> search() {
		
		List<Future<List<FeedId>>> futures = new ArrayList<>();

		futures.add(ExecutorServiceFactory.submit(new Feedable(() -> api.getDirectory())));
		futures.add(ExecutorServiceFactory.submit(new Feedable(() -> api.getDirectoryNoAdult())));
		futures.add(ExecutorServiceFactory.submit(new Feedable(() -> api.getDirectoryClean())));
		futures.add(ExecutorServiceFactory.submit(new Feedable(() -> api.getNew())));
		futures.add(ExecutorServiceFactory.submit(new Feedable(() -> api.getNewNoAdult())));
		futures.add(ExecutorServiceFactory.submit(new Feedable(() -> api.getNewClean())));
		futures.add(ExecutorServiceFactory.submit(new Feedable(() -> api.getMostViewed())));
		futures.add(ExecutorServiceFactory.submit(new Feedable(() -> api.getMostViewedNoAdult())));
		futures.add(ExecutorServiceFactory.submit(new Feedable(() -> api.getMostViewedClean())));
		futures.add(ExecutorServiceFactory.submit(new Feedable(() -> api.getTopRated())));
		futures.add(ExecutorServiceFactory.submit(new Feedable(() -> api.getTopRatedNoAdult())));
		futures.add(ExecutorServiceFactory.submit(new Feedable(() -> api.getTopRatedClean())));
		futures.add(ExecutorServiceFactory.submit(new Feedable(() -> api.getSubscribed())));
		futures.add(ExecutorServiceFactory.submit(new Feedable(() -> api.getSubscribedNoAdult())));
		futures.add(ExecutorServiceFactory.submit(new Feedable(() -> api.getSubscribedClean())));

		List<FeedId> podcasts = new ArrayList<>();
		
		for (Future<List<FeedId>> future : futures) {
			try {
				podcasts.addAll(future.get());
			} catch (InterruptedException | ExecutionException e) {
				logger.warn("getPodcasts() {} : {}", e.getClass().getCanonicalName(), e.getMessage());
			}
		}

		return podcasts.stream();
	}
	
	@Override
	public void search(Consumer<FeedId> consumer) {
		search().forEach(consumer::accept);
	}
	
}
