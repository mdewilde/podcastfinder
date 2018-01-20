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

import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;
import java.util.function.Consumer;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

import be.ceau.podcastfinder.store.api.ExportableFeedProducer;

/**
 * {@link PodcastFinderExport} implementation that writes a JSON format.
 */
public class JsonPodcastFinderExport implements PodcastFinderExport {

	@Override
	public void write(ExportableFeedProducer producer, OutputStream out) {
		Objects.requireNonNull(producer);
		Objects.requireNonNull(out);

		try (JsonWritingExportableFeedConsumer consumer = new JsonWritingExportableFeedConsumer(out)) {
			producer.consumeExportableFeeds(consumer);
		}
	}

	@Override
	public void write(ExportableFeedProducer producer, OutputStream out, String language) {
		Objects.requireNonNull(producer);
		Objects.requireNonNull(out);
		Objects.requireNonNull(language);

		try (JsonWritingExportableFeedConsumer consumer = new JsonWritingExportableFeedConsumer(out)) {
			producer.consumeExportableFeeds(language, consumer);
		}
	}

	private static class JsonWritingExportableFeedConsumer implements Consumer<ExportableFeed>, AutoCloseable {

		private final JsonGenerator generator;

		private JsonWritingExportableFeedConsumer(OutputStream out) {
			try {
				this.generator = new JsonFactory().createGenerator(out, JsonEncoding.UTF8).useDefaultPrettyPrinter();
				this.generator.writeStartArray();
			} catch (IOException e) {
				throw new IllegalStateException(e);
			}
		}

		@Override
		public void accept(ExportableFeed f) {
			try {

				this.generator.writeStartObject();

				this.generator.writeStringField("uri", f.getUri());
				this.generator.writeStringField("name", f.getName());
//				if (f.getDescription() != null) {
//					this.generator.writeStringField("info", f.getDescription());
//				}
				if (f.getUpdated() != null) {
					this.generator.writeStringField("date", f.getUpdated().replaceAll("-", ""));
				}
				this.generator.writeNumberField("items", f.getItems());

				this.generator.writeEndObject();

			} catch (IOException e) {
				throw new IllegalStateException(e);
			}
		}

		@Override
		public void close() {
			try {
				this.generator.writeEndArray();
				this.generator.close();
			} catch (IOException e) {
				throw new IllegalStateException(e);
			}
		}

	}

}
