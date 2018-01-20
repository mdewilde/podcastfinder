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
package be.ceau.podcastfinder.export;

import java.io.OutputStream;

import be.ceau.podcastfinder.store.api.ExportableFeedProducer;

/**
 * Interface describing a class that can export the data held by a podcastfinder instance.
 */
public interface PodcastFinderExport {

	/**
	 * Write a serialized form of all data in this instace to the given {@link OutputStream}
	 * 
	 * @param a
	 *            {@link ExportableFeedProducer}, not {@code null}
	 * @param out
	 *            a {@link OutputStream}, not {@code null}
	 */
	public void write(ExportableFeedProducer producer, OutputStream out);

	/**
	 * Write a serialized form of the data with the given language in this instace to the given
	 * {@link OutputStream}
	 * 
	 * @param a
	 *            {@link ExportableFeedProducer}, not {@code null}
	 * @param out
	 *            a {@link OutputStream}, not {@code null}
	 * @param language
	 *            a {@link String}, not {@code null}
	 */
	public void write(ExportableFeedProducer producer, OutputStream out, String language);

}
