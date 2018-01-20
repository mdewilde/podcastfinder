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
package be.ceau.podcastfinder.executor;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Provide a shared {@link ExecutorService} instance to outside callers.
 */
public class ExecutorServiceFactory {

	private static final int DEFAULT_POOL_SIZE = 124;
	private static final AtomicInteger MAX_CONCURRENCY = new AtomicInteger(DEFAULT_POOL_SIZE);
	private static final ThreadFactory THREAD_FACTORY = new PodcastFinderThreadFactory(MAX_CONCURRENCY);

	private static ThreadPoolExecutor executorService;

	/**
	 * <p>
	 * Set the approximate maximum number of threads that can be executed simultaneously.
	 * </p>
	 * <p>
	 * Setting this number will not have an effect if {@link Runnable} or {@link Callable} tasks have
	 * already been submitted.
	 * </p>
	 * 
	 * @param maxConcurrency
	 *            {@code int}, greater than 0
	 */
	public static void setMaxConcurrency(int maxConcurrency) {
		if (maxConcurrency < 1) {
			throw new IllegalArgumentException("maxConcurrency must be greater than 0");
		}
		MAX_CONCURRENCY.set(maxConcurrency);
	}

	private static synchronized ThreadPoolExecutor get() {
		if (executorService == null) {
			int poolSize = Math.min(MAX_CONCURRENCY.get(), DEFAULT_POOL_SIZE);
			executorService = new ThreadPoolExecutor(poolSize, poolSize, 
					0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(poolSize), 
					THREAD_FACTORY, 
					new ThreadPoolExecutor.CallerRunsPolicy());
		}
		return executorService;
	}

	public static synchronized Future<?> submit(Runnable runnable) {
		return get().submit(runnable);
	}

	public static synchronized <T> Future<T> submit(Callable<T> callable) {
		return get().submit(callable);
	}

	public static int getActiveCount() {
		return get().getActiveCount();
	}
	
	public static int getWaitingCount() {
		return get().getQueue().size();
	}

	public static long getCompletedCount() {
		return get().getCompletedTaskCount();
	}

	/**
	 * Blocking wait until this factory has no more running threads
	 */
	public static void await() {
		while (get().getActiveCount() > 0 && !get().getQueue().isEmpty()) {
			try {
				Thread.sleep(1000l);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				throw new RuntimeException(e);
			}
		}
	}

}
