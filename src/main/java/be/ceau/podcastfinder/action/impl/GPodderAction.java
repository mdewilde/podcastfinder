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

import be.ceau.podcastfinder.action.api.PodcastFinderAction;
import be.ceau.podcastfinder.impl.gpodder.GPodderFinder;
import be.ceau.podcastfinder.store.FeedIdBatchingStore;

public class GPodderAction implements PodcastFinderAction {

	@Override
	public void run() {
		GPodderFinder gpodderFinder = new GPodderFinder();
		try (FeedIdBatchingStore store = new FeedIdBatchingStore(1000)) {
			gpodderFinder.search().forEach(store::add);
		}
	}

}
