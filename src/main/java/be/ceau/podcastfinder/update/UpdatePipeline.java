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
package be.ceau.podcastfinder.update;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.ceau.podcastfinder.executor.ExecutorServiceFactory;
import be.ceau.podcastfinder.http.HttpFeedFetcher;
import be.ceau.podcastfinder.model.PersistedFeed;
import be.ceau.podcastfinder.store.sqlite.PodcastFinderStore;
import be.ceau.podcastfinder.update.filter.NoErrorsFilter;

/**
 * Service class for enriching podcasts known to this application.
 */
public class UpdatePipeline {

	private static final Logger logger = LoggerFactory.getLogger(UpdatePipeline.class);

	private final Queue<PersistedFeed> queue = new ConcurrentLinkedQueue<>();
	private final Predicate<PersistedFeed> feedFilter;

	/**
	 * Default constructor
	 */
	public UpdatePipeline() {
		this(new NoErrorsFilter());
	}

	/**
	 * Constructor
	 * 
	 * @param feedFilter
	 *            {@link Predicate} on {@link PersistedFeed} to decide whether to attempt downloading
	 *            and enriching a specific feed
	 */
	public UpdatePipeline(Predicate<PersistedFeed> feedFilter) {
		Objects.requireNonNull(feedFilter);
		this.feedFilter = feedFilter;
	}

	/**
	 * Start executing the following process:
	 * <ul>
	 * <li>retrieve all podcasts stored by this application in batches
	 * <li>download each feed (if appropriate)
	 * <li>update stored podcast with any new info and store current status or feed error, if any
	 * </ul>
	 */
	public void enrich() {
		try (PodcastFinderStore store = new PodcastFinderStore()) {
			try (PipelineFillingQueue fillingQueue = new PipelineFillingQueue(store)) {
				try (PipelineInserter inserter = new PipelineInserter(store, queue)) {

					// start inserter
					Thread insertThread = new Thread(inserter);
					insertThread.start();

					HttpFeedFetcher httpFeedFetcher = new HttpFeedFetcher();
					
					// start downloads
					List<Future<?>> futures = new LinkedList<>();
					while (!fillingQueue.isFinished()) {
						while (!fillingQueue.isEmpty()) {
							PersistedFeed feed = fillingQueue.get();
							if (feedFilter.test(feed)) {
								futures.add(ExecutorServiceFactory.submit(new PipelineDownloader(feed, queue, httpFeedFetcher)));
							}
						}
					}
					
					futures.forEach(future -> {
						try {
							future.get();
						} catch (InterruptedException | ExecutionException e) {
							logger.warn("InterruptedException waiting for download task to finish");
						}
					});
					
					inserter.stop();
					
					try {
						insertThread.join();
					} catch (InterruptedException e) {
						logger.warn("InterruptedException waiting for insertThread to finish");
						Thread.currentThread().interrupt();
					}
					logger.debug("@end active threads {}", ExecutorServiceFactory.getActiveCount());
					logger.debug("@end waiting count  {}", ExecutorServiceFactory.getWaitingCount());
					logger.debug("@end total threads count {}", ExecutorServiceFactory.getCompletedCount());
					logger.debug("@end errors to insert {}", inserter.getErrorCount());
					logger.debug("@end statuses to insert {}", inserter.getStatusCount());
				}
			}
		}
	}

}
