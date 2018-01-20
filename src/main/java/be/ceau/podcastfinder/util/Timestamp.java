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
package be.ceau.podcastfinder.util;

import static java.time.temporal.ChronoField.DAY_OF_MONTH;
import static java.time.temporal.ChronoField.HOUR_OF_DAY;
import static java.time.temporal.ChronoField.MINUTE_OF_HOUR;
import static java.time.temporal.ChronoField.MONTH_OF_YEAR;
import static java.time.temporal.ChronoField.SECOND_OF_MINUTE;
import static java.time.temporal.ChronoField.YEAR;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility methods for converting {@link LocalDateTime} and {@link LocalDate} objects to and from
 * {@link String} instances for storing in the database
 */
public class Timestamp {

	private static final Logger logger = LoggerFactory.getLogger(Timestamp.class);

	private static final DateTimeFormatter ISO_DATE_TIME = new DateTimeFormatterBuilder()
			.appendValue(YEAR, 4)
			.appendLiteral('-')
			.appendValue(MONTH_OF_YEAR, 2)
			.appendLiteral('-')
			.appendValue(DAY_OF_MONTH, 2)
			.appendLiteral(' ')
			.appendValue(HOUR_OF_DAY, 2)
			.appendLiteral(':')
			.appendValue(MINUTE_OF_HOUR, 2)
			.appendLiteral(':')
			.appendValue(SECOND_OF_MINUTE, 2)
			.toFormatter();

	public static String now() {
		return format(LocalDateTime.now());
	}

	public static String format(LocalDateTime localDateTime) {
		if (localDateTime == null) {
			return null;
		}
		return ISO_DATE_TIME.format(localDateTime);
	}

	public static LocalDateTime parseLocalDateTime(String timestamp) {
		if (timestamp == null) {
			return null;
		}
		try {
			return ISO_DATE_TIME.parse(timestamp, LocalDateTime::from);
		} catch (DateTimeParseException e) {
			logger.error("parseLocalDateTime(String {}) {}: {}", timestamp, e.getClass().getCanonicalName(), e.getMessage());
			return DateTimeFormatter.ISO_LOCAL_DATE_TIME.parse(timestamp, LocalDateTime::from);
		}
	}

	public static String format(LocalDate localDate) {
		if (localDate == null) {
			return null;
		}
		try {
			return DateTimeFormatter.ISO_LOCAL_DATE.format(localDate);
		} catch (DateTimeException e) {
			logger.error("format(LocalDate {}) {}: {}", localDate, e.getClass().getCanonicalName(), e.getMessage());
			return null;
		}
	}

	/**
	 * @param timestamp
	 *            {@link String} or {@code null}
	 * @return {@link LocalDate} or {@code null}
	 */
	public static LocalDate parseLocalDate(String timestamp) {
		if (timestamp == null) {
			return null;
		}
		try {
			return DateTimeFormatter.ISO_LOCAL_DATE.parse(timestamp, LocalDate::from);
		} catch (DateTimeParseException e) {
			logger.error("parseLocalDate(String {}) {}: {}", timestamp, e.getClass().getCanonicalName(), e.getMessage());
			return null;
		}
	}

}
