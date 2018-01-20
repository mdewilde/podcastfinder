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
package be.ceau.podcastfinder.executor;

import java.util.concurrent.ExecutorService;

import org.junit.Assert;
import org.junit.Test;


/**
 * Provide a shared {@link ExecutorService} instance to outside callers.
 */
public class PadTest {

	@Test
	public void pad() {

		int concurrency = 124;
		int pads = 1;
		while ((concurrency = (concurrency / 10)) > 0) {
			pads++;
		}
		Assert.assertEquals(3, pads);

	}

}
