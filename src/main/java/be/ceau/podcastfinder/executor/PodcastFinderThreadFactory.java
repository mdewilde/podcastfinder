package be.ceau.podcastfinder.executor;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;

class PodcastFinderThreadFactory implements ThreadFactory {
	
	private final ThreadGroup group = new ThreadGroup("podcastfinder-pool");
	private final AtomicInteger num = new AtomicInteger(0);
	private final AtomicInteger max;

	PodcastFinderThreadFactory(AtomicInteger maxConcurrency) {
		this.max = maxConcurrency;
	}
	
	@Override
	public Thread newThread(Runnable r) {
		String idx = StringUtils.leftPad(String.valueOf(num.incrementAndGet()), getPad(), '0');
		String name = group.getName() + "-thread-" + idx;
		return new Thread(group, r, name);
	}

	private int getPad() {
		int concurrency = max.get();
		int pads = 1;
		while ((concurrency = (concurrency / 10)) > 0) {
			pads++;
		}
		return pads;
	}
	
}
