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

import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.ceau.podcastfinder.model.PersistedFeed;
import be.ceau.podcastfinder.store.sqlite.PodcastFinderStore;

/**
 * {@link ProducingQueue} implementation providing all {@link PersistedFeed} instances known at
 * instance construction.
 */
class PipelineFillingQueue implements ProducingQueue<PersistedFeed>, AutoCloseable {

	private static final Logger logger = LoggerFactory.getLogger(PipelineFillingQueue.class);

	private static final int AVG_QUEUE_DEPTH = 10000;

	private final BlockingQueue<PersistedFeed> queue = new LinkedBlockingQueue<>(AVG_QUEUE_DEPTH);
	private final RetrieveLoop loop;
	private final Thread loopThread;

	PipelineFillingQueue(PodcastFinderStore store) {
		this(store, AVG_QUEUE_DEPTH);
	}

	PipelineFillingQueue(PodcastFinderStore store, int queueDepth) {
		Objects.requireNonNull(store);
		if (queueDepth < 1) {
			throw new IllegalArgumentException(String.format("queueDepth must be at least 1 but is %s", queueDepth));
		}
		this.loop = new RetrieveLoop(store, queueDepth, queue);
		this.loopThread = new Thread(this.loop);
		this.loopThread.start();
	}

	@Override
	public PersistedFeed get() {
		try {
			// XXX may block indefinitely if loop quits
			return queue.take();
		} catch (InterruptedException e) {
			logger.error("get()", e);
			return null;
		}
	}

	@Override
	public boolean isEmpty() {
		return queue.isEmpty();
	}

	@Override
	public boolean isFinished() {
		return this.loop.finished;
	}

	@Override
	public void close() {
		this.loopThread.interrupt();
	}

}
