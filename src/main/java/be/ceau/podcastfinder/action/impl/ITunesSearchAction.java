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

import be.ceau.itunesapi.request.Country;
import be.ceau.podcastfinder.action.api.PodcastFinderAction;
import be.ceau.podcastfinder.impl.itunes.search.ITunesSearchFinder;
import be.ceau.podcastfinder.impl.itunes.search.SearchTerms;
import be.ceau.podcastfinder.store.sqlite.PodcastFinderStore;

public class ITunesSearchAction implements PodcastFinderAction {

	@Override
	public void run() {
		try (PodcastFinderStore store = new PodcastFinderStore()) {
			ITunesSearchFinder iTunesSearchFinder = new ITunesSearchFinder(Country.UNITED_STATES, SearchTerms.Language.ENGLISH);
			iTunesSearchFinder.search(store::add);
		}
	}

}
