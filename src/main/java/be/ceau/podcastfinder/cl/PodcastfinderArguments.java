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

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Arguments to direct behavior of this application when run stand-alone.
 */
@Command(name = "Podcastfinder")
public class PodcastfinderArguments {

	@Option(names = {"-d", "--digitalpodcast"}, description = {"Search podcasts on digitalpodcast.com"})
	private boolean digitalPodcast = false;
	
	@Option(names = {"-g", "--gpodder"}, description = {"Search podcasts on gpodder.net"})
	private boolean gpodder = false;

	@Option(names = {"-f"}, description = {"Search podcasts on iTunes using the feed generator API"})
	private boolean itunesFeedGenerator = false;

	@Option(names = {"-i"}, description = {"Search podcasts on iTunes using the search API"})
	private boolean itunesSearch = false;

	@Option(names = {"-e", "--enrich"}, description = {"Retrieve podcast feeds to add additional information and current status"})
	private boolean enrich = false;

	@Option(names = {"-c"}, description = {"Maximum number of concurrent downloads"})
	private int concurrency = Integer.MAX_VALUE;

	@Option(names = {"-s", "--status"}, description = {"Show status information about the data held by this application"})
	private boolean status = false;

	@Option(names = {"--dups"}, description = {"Find and display possible duplicate feeds"})
	private boolean duplicates = false;

	@Option(names = {"--clean"}, description = {"Remove all but the newest error statuses and vacuum the database"})
	private boolean clean = false;

	@Option(names = {"--export"}, description = {"Export a list of all valid feeds with at least one episode"})
	private String export = null;

	public boolean isDigitalPodcast() {
		return digitalPodcast;
	}

	public boolean isGpodder() {
		return gpodder;
	}

	public boolean isItunesFeedGenerator() {
		return itunesFeedGenerator;
	}

	public boolean isItunesSearch() {
		return itunesSearch;
	}

	public boolean isEnrich() {
		return enrich;
	}
	
	public int getConcurrency() {
		return concurrency;
	}

	public boolean isStatus() {
		return status;
	}
	
	public boolean isDuplicates() {
		return duplicates;
	}

	public boolean isClean() {
		return clean;
	}

	public String getExport() {
		return export;
	}
	
	public boolean isActive() {
		return digitalPodcast || gpodder || itunesFeedGenerator || itunesSearch || enrich || status || duplicates || clean || export != null;
	}

}
