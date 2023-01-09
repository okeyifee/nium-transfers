package com.nium.interview.transfers.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.runner.RunWith;

import com.nium.interview.transfers.entity.Transfer;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;

@SuppressWarnings("JUnitTestMethodWithNoAssertions")
@RunWith(DataProviderRunner.class)
public class TransferServiceTest {

	static final double LONG_100 = 100.00;
	static final double LONG_60 = 60.00;
	static final double LONG_50 = 50.00;
	static final double LONG_40 = 40.00;
	static final double LONG_30 = 30.00;
	static final double LONG_10 = 10.00;
	static final double LONG_5 = 5.00;

	static final String ACCOUNT_1 = "112233";
	static final String ACCOUNT_2 = "223344";
	static final String ACCOUNT_3 = "334455";
	static final String ACCOUNT_4 = "445566";

	static Transfer transfer1 = null;
	static Transfer transfer2 = null;
	static Transfer transfer3 = null;
	static Transfer transfer4 = null;

	static TransferService transferService = new TransferService();

	@BeforeAll
	static void beforeAll() {

		transfer1 = Transfer.builder()
			.sourceAccount(ACCOUNT_1)
			.destinationAccount(ACCOUNT_2)
			.amount(LONG_100)
			.date(LocalDate.now())
			.transferId("transfer1")
			.build();

		transfer2 = Transfer.builder()
			.sourceAccount(ACCOUNT_1)
			.destinationAccount(ACCOUNT_3)
			.amount(LONG_50)
			.date(LocalDate.now())
			.transferId("transfer2")
			.build();

		transfer3 = Transfer.builder()
			.sourceAccount(ACCOUNT_3)
			.destinationAccount(ACCOUNT_1)
			.amount(LONG_10)
			.date(LocalDate.now())
			.transferId("transfer3")
			.build();

		transfer4 = Transfer.builder()
			.sourceAccount("334455")
			.destinationAccount(ACCOUNT_2)
			.amount(LONG_5)
			.date(LocalDate.now())
			.transferId("transfer4")
			.build();
	}

	@AfterEach
	void afterEach() {
		transferService.transfers = new ArrayList<>();
	}

	@DataProvider
	public static Object[][] transfersDataProvider() {

		return new Object[][]{
			{ new ArrayList<Transfer>(),  new HashMap<>() },
			{ Arrays.asList(transfer1, transfer2, transfer3), Map.of(ACCOUNT_1, LONG_60, ACCOUNT_2, LONG_100, ACCOUNT_3, LONG_40) },
			{ Arrays.asList(transfer1, transfer2, transfer3, transfer4), Map.of(ACCOUNT_1, LONG_60, ACCOUNT_2, LONG_100 + LONG_5, ACCOUNT_3, LONG_30 + LONG_5) }
		};
	}

	@DataProvider
	public static Object[][] frequentSourceAccountDataProvider() {

		return new Object[][]{
			{ new ArrayList<Transfer>(),  null },
			{ Arrays.asList(transfer1, transfer2, transfer3), ACCOUNT_1},
			{ Arrays.asList(transfer1, transfer2, transfer3, transfer4), ACCOUNT_1 }
		};
	}

	@DataProvider
	public static Object[][] transferRecordDataProvider() {

		return new Object[][]{
			{ "" , false },
			{ " .", false },
			{ " " , false },
			{ null , false },
			{ " # Date: 15/08/2055", false },
			{ " 0, 112233, 60.00, 10/08/2055", false },
			{ " 1, er, ab.cd, 10.08.2055, 1-45   ", false },
			{ " 0, 112233, 60.00, 10/08/2055, 1445, 3456", false },
			{ " SOURCE_ACCT, DESTINATION_ACCT, AMOUNT, DATE, TRANSFERID", false },
			{ "0, 112233, 60.00, 10/08/2055, 1445", true },
			{ " 0, 112233, 60.00, 10/08/2055, 1445   ", true },
			{ " 0, 112233, 60.00, 10-08-2055, 1445   ", true },
			{ " 0, 112233, 60.00, 10/08-2055, 1445   ", true },
			{ " 0, 112233, 60.00, 10.08.2055, 1445   ", true }
		};
	}

	@DataProvider
	public static Object[][] balanceDataProvider() {

		final Map<String, Double> accountBalanceMap1 = new LinkedHashMap<>();
		final Map<String, Double> accountBalanceMap2 = new LinkedHashMap<>();
		accountBalanceMap2.put(ACCOUNT_1, LONG_100);
		accountBalanceMap2.put(ACCOUNT_2, LONG_50);
		accountBalanceMap2.put(ACCOUNT_3, LONG_10);

		final Map<String, Double> accountBalanceMap3 = new LinkedHashMap<>();
		accountBalanceMap3.put(ACCOUNT_1, LONG_100);
		accountBalanceMap3.put(ACCOUNT_2, LONG_50);
		accountBalanceMap3.put(ACCOUNT_3, LONG_10);
		accountBalanceMap3.put(ACCOUNT_4, LONG_100);

		final Map<String, Double> accountBalanceMap4= new LinkedHashMap<>();
		accountBalanceMap4.put(ACCOUNT_1, LONG_100);

		return new Object[][]{
			{ accountBalanceMap1, null },
			{ accountBalanceMap2, ACCOUNT_1 },
			{ accountBalanceMap3, ACCOUNT_1 },
			{ accountBalanceMap4, ACCOUNT_1 },
		};
	}

	@ParameterizedTest
	@MethodSource("transferRecordDataProvider")
	void saveTransferRecordInList(final String transferRecordLine,  final boolean isValidTransferRecord) {
		final int sizeBeforeSave = transferService.transfers.size();
		transferService.saveTransferRecordInList(transferRecordLine);

		assertEquals(transferService.transfers.size(), isValidTransferRecord ? sizeBeforeSave + 1 : sizeBeforeSave);
	}

	@ParameterizedTest
	@MethodSource("frequentSourceAccountDataProvider")
	void getFrequentlyUsedSourceAccount(final List<Transfer> transfers, final String expectedSourceAccount) {

		transferService.transfers = Collections.unmodifiableList(transfers);
		final String frequentlyUsedSourceAccount = transferService.getFrequentlyUsedSourceAccount();

		assertEquals(expectedSourceAccount, frequentlyUsedSourceAccount);
	}

	@ParameterizedTest
	@MethodSource("transfersDataProvider")
	void calculateAccountBalance(final List<Transfer> transfers, final Map<String, Double> expectedMapOfAccountBalances) {

		transferService.transfers = Collections.unmodifiableList(transfers);
		transferService.accountBalanceMap = new LinkedHashMap<>();
		final Map<String, Double> accountBalance = transferService.calculateAccountBalance();

		assertEquals(expectedMapOfAccountBalances, accountBalance);
	}

	@ParameterizedTest
	@MethodSource("balanceDataProvider")
	void getAccountWithHighestBalance(final Map<String, Double> accountBalanceMap, final String expectedAccount) {

		transferService.accountBalanceMap = Collections.unmodifiableMap(accountBalanceMap);
		final String accountWithHighestBalance = transferService.getAccountWithHighestBalance();

		assertEquals(expectedAccount, accountWithHighestBalance);
	}
}
