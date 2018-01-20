package be.ceau.podcastfinder.locallinksaver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import be.ceau.podcastfinder.api.PathProvider;
import be.ceau.podcastfinder.util.LocalLinkSaver;

public class LocalLinkSaverTest {

	@Test
	public void test() throws IOException {
		LocalLinkSaver localLinkSaver = new LocalLinkSaver(PathProvider.get());

		List<URI> links = new ArrayList<>();
		try (InputStream in = LocalLinkSaverTest.class.getClassLoader().getResourceAsStream("invalidfeeds.log");
				InputStreamReader is = new InputStreamReader(in, StandardCharsets.UTF_8);
				BufferedReader br = new BufferedReader(is);) {
			String line;
			while ((line = br.readLine()) != null) {
				try {
					links.add(URI.create(line));
				} catch (Exception e) {
					e.printStackTrace(System.err);
				}
			}

			try {
				localLinkSaver.save(links);
			} catch (Exception e) {
				e.printStackTrace(System.err);
			}

		} catch (IOException e) {
			e.printStackTrace(System.err);
		}

	}

}
