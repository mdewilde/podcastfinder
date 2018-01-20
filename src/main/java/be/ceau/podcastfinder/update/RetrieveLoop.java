package be.ceau.podcastfinder.update;

import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.ceau.podcastfinder.model.PersistedFeed;
import be.ceau.podcastfinder.store.sqlite.PodcastFinderStore;

class RetrieveLoop implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(RetrieveLoop.class);

	private final PodcastFinderStore store;
	private final BlockingQueue<PersistedFeed> queue;
	private final int queueDepth;
	boolean finished = false;

	RetrieveLoop(PodcastFinderStore store, int queueDepth, BlockingQueue<PersistedFeed> queue) {
		this.store = store;
		this.queueDepth = queueDepth;
		this.queue = queue;
	}

	@Override
	public void run() {
		int iterations = determineIterationCount();
		loop(iterations, store);
		this.finished = true;
		logger.debug("RetrieveLoop.run() finished");
	}

	private int determineIterationCount() {
		int maxPodcastId = store.getMaxPodcastId();
		int iterations = maxPodcastId / queueDepth;
		if (maxPodcastId % queueDepth != 0) {
			iterations++;
		}
		return iterations;
	}

	private void loop(int iterations, PodcastFinderStore store) {
		int displayedPercentage = -1;
		int minPodcastId, maxPodcastId;
		for (int i = 0; i < iterations; i++) {
			minPodcastId = i * queueDepth;
			maxPodcastId = (i + 1) * queueDepth;

			store.getPersistedFeeds(minPodcastId, maxPodcastId)
					.stream()
					.forEach(f -> {
						try {
							queue.put(f);
						} catch (InterruptedException e) {
							logger.error("loop(int {}, PodcastFinderStore)", iterations, store, e);
							this.finished = true;
						}
					});

			int percentage = 0;
			if (i > 0) {
				percentage = (int) ((i / (double) iterations) * 100);
				if (percentage % 5 == 0 && percentage != displayedPercentage) {
					displayedPercentage = percentage;
					logger.trace("PipelineFillingQueue: offered {}% of available feeds", percentage);
				}
			}
		}
	}

}