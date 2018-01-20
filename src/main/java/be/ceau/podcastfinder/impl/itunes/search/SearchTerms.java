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
package be.ceau.podcastfinder.impl.itunes.search;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class SearchTerms {

	/**
	 * Enumeration of languages supported by {@link ITunesSearchFinder}
	 */
	public static enum Language {

		ENGLISH, DUTCH;

	}

	public static List<String> get(Language language) {
		Objects.requireNonNull(language);
		if (language == Language.ENGLISH) {
			return getTermsEn();
		}
		if (language == Language.DUTCH) {
			return getTermsNl();
		}
		throw new IllegalStateException(String.format("no strategy for %s implemented", language));
	}

	public static List<String> getTermsEn() {
		return read("terms_en.txt");
	}

	public static List<String> getTermsNl() {
		return read("terms_nl.txt");
	}

	private static List<String> read(String file) {
		List<String> words = new ArrayList<>();
		try (InputStream in = SearchTerms.class.getClassLoader().getResourceAsStream(file);
				InputStreamReader is = new InputStreamReader(in, StandardCharsets.UTF_8);
				BufferedReader br = new BufferedReader(is);) {
			String line;
			while ((line = br.readLine()) != null) {
				words.add(line);
			}
			return Collections.unmodifiableList(words);
		} catch (IOException e) {
			throw new IllegalStateException("loading SearchTerms did not complete successfully", e);
		}
	}
	
}
