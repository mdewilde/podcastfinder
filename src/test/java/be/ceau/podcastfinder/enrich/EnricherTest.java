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
package be.ceau.podcastfinder.enrich;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.client.ClientProtocolException;
import org.junit.Test;

import be.ceau.podcastfinder.http.HttpStringFetcher;
import be.ceau.podcastfinder.update.UpdatePipeline;

public class EnricherTest {

//	@Test
	public void enrich() {
		new UpdatePipeline().enrich();
	}

	@Test
	public void utfTest() throws ClientProtocolException, IOException, URISyntaxException {
		String url = "http://podcast8.kiqtas.jp/madoca/index.xml";
		
		String xml = new HttpStringFetcher().get(new URI(url));

		System.out.println(xml);
		
	}
	
}
