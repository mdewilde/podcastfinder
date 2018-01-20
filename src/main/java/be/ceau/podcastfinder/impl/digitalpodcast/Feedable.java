package be.ceau.podcastfinder.impl.digitalpodcast;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.ceau.podcastfinder.model.FeedId;

class Feedable implements Callable<List<FeedId>> {

	private static final Logger logger = LoggerFactory.getLogger(Feedable.class);

	private final Supplier<Stream<be.ceau.digitalpodcast.Podcast>> supplier;

	Feedable(Supplier<Stream<be.ceau.digitalpodcast.Podcast>> supplier) {
		this.supplier = supplier;
	}

	@Override
	public List<FeedId> call() {
		return supplier.get()
				.map(p -> {
					try {
						return new FeedId(p.getName(), p.getUrl());
					} catch (IllegalArgumentException e) {
						logger.warn("ignoring podcast with invalid URI {}", p.getUrl());
						return null;
					}
				})
				.filter(Objects::nonNull)
				.collect(Collectors.toList());
	}

}