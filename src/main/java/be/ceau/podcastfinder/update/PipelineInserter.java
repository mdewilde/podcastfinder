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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.ceau.podcastfinder.model.FeedError;
import be.ceau.podcastfinder.model.FeedStatus;
import be.ceau.podcastfinder.model.PersistedFeed;
import be.ceau.podcastfinder.store.sqlite.PodcastFinderStore;

/**
 * Insert items from queue into database.
 * 
 * Instances are not threadsafe. Create a new instance per thread.
 */
class PipelineInserter implements Runnable, AutoCloseable {

	private static final Logger logger = LoggerFactory.getLogger(PipelineInserter.class);

	private static final int THRESHOLD = 33;

	private final PodcastFinderStore store;
	private boolean stop = false;

	private final Queue<PersistedFeed> queue;

	private final List<FeedStatus> statuses = new LinkedList<>();
	private final List<FeedError> errors = new LinkedList<>();

	public PipelineInserter(PodcastFinderStore store, Queue<PersistedFeed> queue) {
		Objects.requireNonNull(store);
		Objects.requireNonNull(queue);
		this.store = store;
		this.queue = queue;
	}

	@Override
	public void run() {
		while (!stop) {
			dispatch();
			insertAboveThreshold(THRESHOLD);
			if (queue.isEmpty()) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					logger.warn("PipelineInserter.run() interrupted");
					close();
					break;
				}
			}
		}
		Thread.currentThread().interrupt();
	}

	public int getStatusCount() {
		return statuses.size();
	}

	public int getErrorCount() {
		return errors.size();
	}

	public void stop() {
		stop = true;
	}

	@Override
	public void close() {
		stop = true;
		insertAboveThreshold(0);
	}

	private void dispatch() {
		PersistedFeed feed = queue.poll();
		if (feed == null) {
			return;
		}
		if (!feed.getFeedStatuses().isEmpty()) {
			statuses.addAll(feed.getFeedStatuses());
			store.update(feed.getPodcastId(), feed.getFeedInfo());
		}
		if (!feed.getFeedErrors().isEmpty()) {
			errors.addAll(feed.getFeedErrors());
		}
	}

	/**
	 * Move and insert all {@link FeedStatus} instances and all {@link FeedError} instances to database.
	 * 
	 * @param threshold
	 *            the minimum number of statuses and errors to insert
	 */
	private void insertAboveThreshold(int threshold) {
		if (statuses.size() > threshold) {
			store.addFeedStatuses(statuses);
			statuses.clear();
		}
		if (errors.size() > threshold) {
			store.addFeedErrors(errors);
			errors.clear();
		}
	}

}
