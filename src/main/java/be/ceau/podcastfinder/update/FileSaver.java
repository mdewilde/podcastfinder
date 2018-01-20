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
package be.ceau.podcastfinder.update;

import java.io.IOException;

public interface FileSaver {

	/**
	 * Best effort saving text into a file in the appropriate directory for this application
	 * 
	 * @param name
	 *            filename {@link String}, without extension, can not be {@code null}
	 * @param text
	 *            contents {@link String}, can be {@code null}
	 */
	public void save(String name, String text) throws IOException;

}
