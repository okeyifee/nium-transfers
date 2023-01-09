package com.nium.interview.transfers.util;

import static com.nium.interview.transfers.util.DateUtils.getDate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.runner.RunWith;

import com.nium.interview.transfers.exception.TransferException;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;

@RunWith(DataProviderRunner.class)
class DateUtilsTest {

	public static final int YEAR = 2055;
	public static final int DAY = 13;
	public static final int MONTH = 8;

	@DataProvider
	public static Object[][] dateDataProvider() {
		return new Object[][]{
			{ "13/08/2055", LocalDate.of(YEAR, MONTH, DAY) },
			{ " 13/08/2055", LocalDate.of(YEAR, MONTH, DAY) },
			{ " 13/08/2055 ", LocalDate.of(YEAR, MONTH, DAY) },
			{ "13/08/2055 ", LocalDate.of(YEAR, MONTH, DAY) },
			{ "13-08-2055", LocalDate.of(YEAR, MONTH, DAY) },
			{ null, null }
		};
	}

	@DataProvider
	public static String[][] invalidDateDataProvider() {
		return new String[][]{
			{ "13/08/20@5" },
			{ " 13/08/20-5" },
			{ " 1308/2055 " },
			{ "13/-8/2055 " },
			{ "13 8 2055 " },
			{ "13 - 08 - 2055" },
			{ "13,08-2055" }
		};
	}

	@ParameterizedTest
	@MethodSource("dateDataProvider")
	void getDateWithValidDateStringTest(final String stringDate, final LocalDate expectedDate) {
		final LocalDate actualLocalDate = getDate(stringDate);
		assertEquals(actualLocalDate, expectedDate);
	}

	@ParameterizedTest
	@MethodSource("invalidDateDataProvider")
	void getDateWithInvalidDateStringTest(final String stringDate) {

		final Exception exception = assertThrows(TransferException.class, () -> getDate(stringDate));

		assertEquals("unable to parse dateString - " + stringDate, exception.getMessage());
	}
}
