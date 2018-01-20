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
package be.ceau.podcastfinder.model;

import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import be.ceau.podcastparser.util.Strings;

/**
 * Instances contain two or more possibly duplicate feeds
 */
public class Duplicates {

	private final int items;
	private final int hash;
	private final int bytes;
	private final TreeSet<Dup> dups;

	public Duplicates(int items, int hash, int bytes, Collection<Dup> dups) {
		if (items < 0) {
			throw new IllegalArgumentException("items must be 0 or greater");
		}
		if (bytes < 0) {
			throw new IllegalArgumentException("bytes must be 0 or greater");
		}
		if (dups.size() < 2) {
			throw new IllegalArgumentException("number of dups must be 2 or greater");
		}
		this.items = items;
		this.hash = hash;
		this.bytes = bytes;
		this.dups = new TreeSet<>(dups);
	}

	public int getItems() {
		return items;
	}

	public int getHash() {
		return hash;
	}

	public int getBytes() {
		return bytes;
	}

	public void add(Dup dup) {
		if (dup != null) {
			dups.add(dup);
		}
	}

	public void addAll(Collection<Dup> dups) {
		if (dups != null) {
			dups.forEach(this::add);
		}
	}

	public Set<Dup> getDups() {
		return Collections.unmodifiableSet(dups);
	}
	
	public int getLowestPodcastId() {
		return dups.first().getPodcastId();
	}
	
	private String toConsole(Dup dup) {
		String desc = Strings.reduceWhitespace(dup.getDescription());
		return new StringBuilder()
				.append("internal id         | ")
				.append(dup.getPodcastId())
				.append(System.lineSeparator())
				.append("uri                 | ")
				.append(dup.getUri())
				.append(System.lineSeparator())
				.append("name                | ")
				.append(dup.getName())
				.append(System.lineSeparator())
				.append("language            | ")
				.append(dup.getLanguage() == null ? "" : dup.getLanguage())
				.append(System.lineSeparator())
				.append("description         | ")
				.append(desc == null ? "" : desc)
				.toString();
	}

	public String toConsole() {
		String separator = System.lineSeparator() + "-------------------------------------" + System.lineSeparator();
		return System.lineSeparator() 
			+ "--------------------------------------------------------------------------"
			+ System.lineSeparator()
			+ dups.size() + " apparent duplicate feeds"
			+ System.lineSeparator()
			+ "-------------------------------------"
			+ System.lineSeparator()
			+ dups.stream()
				.map(this::toConsole)
				.collect(Collectors.joining(separator))
			+ System.lineSeparator()
			+ "-------------------------------------"
			+ System.lineSeparator()
			+ "ids to remove " + getRemovableIds()
			+ System.lineSeparator()
			+ "--------------------------------------------------------------------------"
			+ System.lineSeparator();
	}

	public Set<Integer> getRemovableIds() {
		return dups.stream()
			.filter(dup -> {
				return lowerPodcastIdWithSameLinkExists(dup) || httpLinkExists(dup);
			})
			.map(Dup::getPodcastId)
			.collect(Collectors.toSet());
	}
	
	private boolean lowerPodcastIdWithSameLinkExists(final Dup dup) {
		return dups.stream()
				.filter(d2 -> dup.getPodcastId() > d2.getPodcastId())
				.anyMatch(d2 -> dup.getUri().equals(d2.getUri()));
	}
	
	private boolean httpLinkExists(final Dup dup) {
		if (!"http".equals(dup.getUri().getScheme())) {
			return false;
		}
		return dups.stream()
				.filter(d2 -> dup.getPodcastId() != d2.getPodcastId())
				.filter(d2 -> "https".equals(d2.getUri().getScheme()))
				.anyMatch(d2 -> {
					String da = StringUtils.removeStart(dup.getUri().toASCIIString(), "http://");
					String db = StringUtils.removeStart(d2.getUri().toASCIIString(), "https://");
					return da.equals(db);
				});
	}

	@Override
	public int hashCode() {
		return 31 + dups.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Duplicates other = (Duplicates) obj;
		if (dups == null) {
			if (other.dups != null)
				return false;
		} else if (!dups.equals(other.dups))
			return false;
		return true;
	}

	public static class Dup implements Comparable<Dup> {

		private int podcastId;
		private URI uri;
		private String name;
		private String language;
		private String description;

		public int getPodcastId() {
			return podcastId;
		}

		public void setPodcastId(int podcastId) {
			this.podcastId = podcastId;
		}

		public URI getUri() {
			return uri;
		}

		public void setUri(URI uri) {
			this.uri = uri;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getLanguage() {
			return language;
		}

		public void setLanguage(String language) {
			this.language = language;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		@Override
		public int compareTo(Dup other) {
			return this.podcastId - other.podcastId;
		}

		@Override
		public int hashCode() {
			return 31 + podcastId;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Dup other = (Dup) obj;
			if (podcastId != other.podcastId)
				return false;
			return true;
		}
		
	}

}
