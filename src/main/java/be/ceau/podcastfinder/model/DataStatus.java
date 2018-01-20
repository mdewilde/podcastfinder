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

import java.text.DecimalFormat;

import org.apache.commons.lang3.StringUtils;

/**
 * Instances contain status information about all the data held by this application
 */
public class DataStatus {

	private int total;
	private int validated;
	private int zeroItems;
	private int failed;
	private int unverified;
	private int enclosures;

	/**
	 * @return {@code int} total number of podcasts stored by this library
	 */
	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	/**
	 * @return {@code int} number of podcasts with valid feeds containing at least one item
	 */
	public int getValidated() {
		return validated;
	}

	public void setValidated(int validated) {
		this.validated = validated;
	}

	/**
	 * @return {@code int} number of podcasts with valid feeds containing 0 items
	 */
	public int getZeroItems() {
		return zeroItems;
	}

	public void setZeroItems(int zeroItems) {
		this.zeroItems = zeroItems;
	}

	/**
	 * @return {@code int} number of podcasts for which no valid feed could be downloaded
	 */
	public int getFailed() {
		return failed;
	}

	public void setFailed(int failed) {
		this.failed = failed;
	}

	/**
	 * @return {@code int} number of podcasts for which no download attemps were made
	 */
	public int getUnverified() {
		return unverified;
	}

	public void setUnverified(int unverified) {
		this.unverified = unverified;
	}

	/**
	 * @return {@code int} sum of enclosures for all feeds in this library
	 */
	public int getEnclosures() {
		return enclosures;
	}

	public void setEnclosures(int enclosures) {
		this.enclosures = enclosures;
	}

	public String toConsole() {
		final DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
		final int rightPad = 40;
		final int leftPad = 14;
		final int dashes = rightPad + leftPad + 5;
		return new StringBuilder()
				.append(StringUtils.repeat('-', dashes))
				.append(System.lineSeparator())

				.append("| ")
				.append(StringUtils.rightPad("Total number of podcast feeds", rightPad))
				.append("|")
				.append(StringUtils.leftPad(decimalFormat.format(total), leftPad))
				.append(" |")

				.append(System.lineSeparator())
				.append(StringUtils.repeat('-', dashes))
				.append(System.lineSeparator())

				.append("| ")
				.append(StringUtils.rightPad("Number of valid feeds with items", rightPad))
				.append("|")
				.append(StringUtils.leftPad(decimalFormat.format(validated), leftPad))
				.append(" |")

				.append(System.lineSeparator())
				.append(StringUtils.repeat('-', dashes))
				.append(System.lineSeparator())

				.append("| ")
				.append(StringUtils.rightPad("Number of valid feeds with 0 items", rightPad))
				.append("|")
				.append(StringUtils.leftPad(decimalFormat.format(zeroItems), leftPad))
				.append(" |")
				
				.append(System.lineSeparator())
				.append(StringUtils.repeat('-', dashes))
				.append(System.lineSeparator())

				.append("| ")
				.append(StringUtils.rightPad("Number of invalid feeds", rightPad))
				.append("|")
				.append(StringUtils.leftPad(decimalFormat.format(failed), leftPad))
				.append(" |")
				
				.append(System.lineSeparator())
				.append(StringUtils.repeat('-', dashes))
				.append(System.lineSeparator())
				
				.append("| ")
				.append(StringUtils.rightPad("Number of unverified feeds", rightPad))
				.append("|")
				.append(StringUtils.leftPad(decimalFormat.format(unverified), leftPad))
				.append(" |")

				.append(System.lineSeparator())
				.append(StringUtils.repeat('-', dashes))
				.append(System.lineSeparator())

				.append("| ")
				.append(StringUtils.rightPad("Total number of enclosures", rightPad))
				.append("|")
				.append(StringUtils.leftPad(decimalFormat.format(enclosures), leftPad))
				.append(" |")
				
				.append(System.lineSeparator())
				.append(StringUtils.repeat('-', dashes))
				
				.toString();
	}
	
}
