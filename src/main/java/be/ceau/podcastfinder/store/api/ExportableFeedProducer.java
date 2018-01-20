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
package be.ceau.podcastfinder.store.api;

import java.util.List;
import java.util.function.Consumer;

import be.ceau.podcastfinder.export.ExportableFeed;

public interface ExportableFeedProducer {

	public List<ExportableFeed> getExportableFeeds();

	public List<ExportableFeed> getExportableFeeds(String language);

	public void consumeExportableFeeds(Consumer<ExportableFeed> consumer);

	public void consumeExportableFeeds(String language, Consumer<ExportableFeed> consumer);

}
