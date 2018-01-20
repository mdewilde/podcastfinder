package be.ceau.podcastfinder.util;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.ceau.podcastfinder.http.HttpStringFetcher;

class SavingRunnable implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(LocalLinkSaver.class);

	private final Path path;
	private final URI uri;
	private final HttpStringFetcher httpStringFetcher;

	SavingRunnable(Path path, URI uri, HttpStringFetcher httpStringFetcher) {
		this.path = path;
		this.uri = uri;
		this.httpStringFetcher = httpStringFetcher;
	}

	@Override
	public void run() {
		String fileName = toFileName();
		Path file = path.resolve(fileName);
		if (Files.exists(file)) {
			return;
		}
		try {
			String xml = httpStringFetcher.get(uri);
			file = Files.createFile(file);
			Files.write(file, xml.getBytes(StandardCharsets.UTF_8));
		} catch (Exception e) {
			logger.error("run() {}", fileName, e);
		}
	}

	private String toFileName() {
		String filename = uri.toASCIIString();
		filename = StringUtils.substringAfter(filename, "://");
		filename = StringUtils.replace(filename, "/", "_");
		filename = StringUtils.substring(filename, 0, 150);
		return filename + ".xml";
	}
	
}