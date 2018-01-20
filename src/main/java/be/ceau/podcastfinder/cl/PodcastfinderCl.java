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
package be.ceau.podcastfinder.cl;

import be.ceau.podcastfinder.action.impl.CleanAction;
import be.ceau.podcastfinder.action.impl.DigitalPodcastAction;
import be.ceau.podcastfinder.action.impl.DuplicatesAction;
import be.ceau.podcastfinder.action.impl.ExportAction;
import be.ceau.podcastfinder.action.impl.GPodderAction;
import be.ceau.podcastfinder.action.impl.ITunesFeedGeneratorAction;
import be.ceau.podcastfinder.action.impl.ITunesSearchAction;
import be.ceau.podcastfinder.executor.ExecutorServiceFactory;
import be.ceau.podcastfinder.store.sqlite.PodcastFinderStore;
import be.ceau.podcastfinder.update.UpdatePipeline;
import be.ceau.podcastfinder.update.filter.UpdateFilter;
import picocli.CommandLine;

/**
 * Entry point to execute this program as a standalone Java application.
 */
public class PodcastfinderCl {

	public static void main(String[] args) {
		
		PodcastfinderArguments arguments = new PodcastfinderArguments();

		try {
			CommandLine.populateCommand(arguments, args);
		} catch (Exception e) {
			CommandLine.usage(arguments, System.out);
			System.exit(1);
		}

		if (!arguments.isActive()) {
			CommandLine.usage(arguments, System.out);
			System.exit(0);
		}

		ExecutorServiceFactory.setMaxConcurrency(arguments.getConcurrency());

		try {
			if (arguments.isDuplicates()) {
				new DuplicatesAction().run();
			}
			if ("json".equals(arguments.getExport())) {
				System.out.println("starting podcast export");
				new ExportAction().run();
			}
			if (arguments.isDigitalPodcast()) {
				System.out.println("starting digital podcast lookup");
				new DigitalPodcastAction().run();
			}
			if (arguments.isGpodder()) {
				System.out.println("starting gpodder lookup");
				new GPodderAction().run();
			}
			if (arguments.isItunesFeedGenerator()) {
				System.out.println("starting iTunes feed generator lookup");
				new ITunesFeedGeneratorAction().run();
			}
			if (arguments.isItunesSearch()) {
				System.out.println("starting iTunes search lookup");
				new ITunesSearchAction().run();
			}
			if (arguments.isEnrich()) {
				new UpdatePipeline(new UpdateFilter()).enrich();
			}
			if (arguments.isClean()) {
				new CleanAction().run();
			}
			if (arguments.isStatus()) {
				try (PodcastFinderStore store = new PodcastFinderStore()) {
					System.out.println(store.getDataStatus().toConsole());
				}
			}
		} catch (Exception e) {
			e.printStackTrace(System.err);
			System.exit(1);
		}
		System.exit(0);
	}

}
