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
package be.ceau.podcastfinder.util;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.ceau.podcastfinder.executor.ExecutorServiceFactory;
import be.ceau.podcastfinder.http.HttpStringFetcher;

/**
 * Saves one or more URIs to local files
 */
public class LocalLinkSaver {

	private static final Logger logger = LoggerFactory.getLogger(LocalLinkSaver.class);

	private final Path path;
	
	private final HttpStringFetcher httpStringFetcher;
	
	public LocalLinkSaver(Path path, HttpStringFetcher httpStringFetcher) throws IOException {
		Objects.requireNonNull(path);
		Objects.requireNonNull(httpStringFetcher);
		this.path = path.resolve("savedlinks");
		if (Files.notExists(this.path) && Files.createDirectories(this.path) == null) {
			throw new IllegalArgumentException(String.format("could not create path %s", path));
		}
		this.httpStringFetcher = httpStringFetcher;
	}

	public LocalLinkSaver(Path path) throws IOException {
		this(path, new HttpStringFetcher());
	}
	
	public void save(URI uri) {
		new SavingRunnable(path, uri, httpStringFetcher).run();
	}

	public void save(Collection<URI> uris) {
		uris.stream()
				.map(uri -> new SavingRunnable(path, uri, httpStringFetcher))
				.forEach(ExecutorServiceFactory::submit);
		ExecutorServiceFactory.await();
		return;
	}

}
