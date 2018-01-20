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

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConcurrentBatchingStore implements AutoCloseable {

	private static final Logger logger = LoggerFactory.getLogger(ConcurrentBatchingStore.class);

	private static final Lock PERSIST_LOCK = new ReentrantLock();

//	private final ConcurrentLinkedQueue<T> queue = new ConcurrentLinkedQueue<>();
//	private final Store<T> store;
//	private final int batchSize;
//	private final AtomicBoolean closed = new AtomicBoolean(false);
//	
//	public ConcurrentBatchingStore(Store<T> store) {
//		this(store, 250);
//	}
//
//	public ConcurrentBatchingStore(Store<T> store, int batchSize) {
//		Objects.requireNonNull(store);
//		if (batchSize < 1) {
//			throw new IllegalArgumentException(String.format("batchSize must be at least 1 but is %s"));
//		}
//		this.store = store;
//		this.batchSize = batchSize;
//	}
//
//	/**
//	 * Add {@link PodcastLink} to be persisted eventually. This method is single-threaded and blocking.
//	 * 
//	 * @param link
//	 *            a {@link PodcastLink}, not {@code null}
//	 */
//	@Override
//	public void add(T link) {
//		Objects.requireNonNull(link);
//		if (closed.get()) {
//			throw new IllegalStateException("this MultiThreadedBatchPersister has been closed");
//		}
//		queue.add(link);
//		if (queue.size() >= batchSize) {
//			if (PERSIST_LOCK.tryLock()) {
//				try {
//					if (queue.size() >= batchSize) {
//						List<T> list = new ArrayList<>();
//						for (int i = 0; i < batchSize; i++) {
//							list.add(queue.poll());
//						}
//						persist(list);
//					}
//				} finally {
//					PERSIST_LOCK.unlock();
//				}
//			}
//		}
//	}
//
//	@Override
//	public void add(Collection<T> links) {
//		links.forEach(this::add);
//	}
//
//	@Override
//	public void update(T link) {
//		store.update(link);
//	}
//
//	@Override
//	public List<T> list() {
//		return store.list();
//	}

	@Override
	public void close() {
		// perform close operations only once
//		if (closed.compareAndSet(false, true)) {
//			// no new podcasts can be added now
//			if (!queue.isEmpty()) {
//				persist(queue);
//			}
//		}
	}

//	private void persist(Collection<T> collection) {
//		PERSIST_LOCK.lock();
//		try {
//			Iterator<T> iterator = collection.iterator();
//			List<T> list = new ArrayList<>();
//			while (iterator.hasNext()) {
//				list.add(iterator.next());
//				if (list.size() >= batchSize) {
//					logger.trace("persist(List<T>) storing {} podcasts", list.size());
//					store.add(list);
//					list.clear();
//				}
//			}
//			if (!list.isEmpty()) {
//				logger.trace("persist(List<T>) storing {} podcasts", list.size());
//				store.add(list);
//			}
//			collection.clear();
//		} finally {
//			PERSIST_LOCK.unlock();
//		}
//	}

}
