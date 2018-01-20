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
package be.ceau.podcastfinder.action.impl;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import be.ceau.podcastfinder.action.api.PodcastFinderAction;
import be.ceau.podcastfinder.export.JsonPodcastFinderExport;
import be.ceau.podcastfinder.store.sqlite.PodcastFinderStore;

public class ExportAction implements PodcastFinderAction {

	@Override
	public void run() {
		Path dir = Paths.get(System.getProperty("user.home"), "podcast-export", "json");
		try {
			Files.createDirectories(dir);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		try (PodcastFinderStore store = new PodcastFinderStore()) {

			store.getLanguages().forEach(lang -> {

				Path path = dir.resolve("podcastfinder-export-" + lang + ".json");
				try {
					Files.createFile(path);
				} catch (IOException e1) {
					e1.printStackTrace();
				}

				try (FileOutputStream out = new FileOutputStream(path.toFile())) {
					new JsonPodcastFinderExport().write(store, out, lang);
				} catch (IOException e) {
					e.printStackTrace();
				}

			});

		}
	}

}
