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
package be.ceau.podcastfinder.store;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import be.ceau.podcastfinder.store.sqlite.PodcastFinderStore;

/**
 * Non-threadsafe wrapper around {@link PodcastFinderStore} that gathers and collects objects
 * instances for batched insertion into a wrapped {@link Store}
 */
public abstract class BatchingStore<T> implements AutoCloseable {

	protected static final int DEFAULT_BATCH_SIZE = 250;

	protected final PodcastFinderStore store = new PodcastFinderStore();
	protected final int batchSize;
	protected final List<T> instances = new ArrayList<>();

	/**
	 * Construct a new {@link BatchingStore} with default batch size {@value #DEFAULT_BATCH_SIZE}
	 * 
	 * @param store
	 *            the {@link Store} to wrap, not {@code null}
	 */
	public BatchingStore() {
		this(DEFAULT_BATCH_SIZE);
	}

	public BatchingStore(int batchSize) {
		Objects.requireNonNull(store);
		if (batchSize < 1) {
			throw new IllegalArgumentException(String.format("batchSize must be at least 1 but is %s", batchSize));
		}
		this.batchSize = batchSize;
	}

	/**
	 * Add object to be persisted eventually. This method is single-threaded and blocking.
	 * 
	 * @param link
	 *            a {@link T}, not {@code null}
	 */
	public void add(T t) {
		instances.add(t);
		if (instances.size() >= batchSize) {
			persist();
		}
	}

	public void add(Collection<T> ts) {
		ts.forEach(this::add);
	}

	public abstract void persist();

	@Override
	public void close() {
		persist();
	}

}
