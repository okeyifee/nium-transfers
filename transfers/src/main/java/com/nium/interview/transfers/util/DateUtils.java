package com.nium.interview.transfers.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.nium.interview.transfers.exception.TransferException;

/**
 * Utility class for date conversion
 * @author okezie okechukwu
 */
public final class DateUtils {

	private static final Pattern PATTERN = Pattern.compile("[.-]");

	private DateUtils() {
	}

	/**
	 * @param dateString the date to be converted in string format.
	 * @return the formatted date in LocalDate ("dd-MM-yyyy") or null if dateString is empty or null.
	 */
	public static LocalDate getDate(final String dateString) {

		try {
			return null == dateString || StringUtils.isBlank(dateString) ? null
				: LocalDate.parse(PATTERN.matcher(dateString).replaceAll("/").trim(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
		} catch (final DateTimeParseException ex) {
			throw new TransferException("unable to parse dateString - " + dateString, ex);
		}
	}
}
