package com.nium.interview.transfers.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.nium.interview.transfers.entity.Transfer;
import com.nium.interview.transfers.util.DateUtils;

import lombok.extern.log4j.Log4j2;

/**
 * Transfer service
 * @author okezie okechukwu
 */
@Log4j2
public class TransferService {

	private static final String SUBTRACT = "SUBTRACT";
	private static final String ADD = "ADD";
	private static final String ZERO_BALANCE = "0";

	List<Transfer> transfers = new ArrayList<>();
	Map<String, Double> accountBalanceMap = new LinkedHashMap<>();

	/**
	 * @param line line from the txt file.
	 */
	public void saveTransferRecordInList(final String line) {

		if (!StringUtils.isBlank(line) && !line.trim().startsWith("#") && !line.contains("_"))  {
			final String[] parts = line.split(",");

			if (5 != parts.length) {
				log.error("Invalid transfer record - {}", line);
				return;
			}

			final String amountString = parts[2].trim();

			if (!NumberUtils.isParsable(amountString)) {
				log.error("Invalid transfer record - {}", line);
				return;
			}

			final Transfer transfer = Transfer.builder()
				.sourceAccount(parts[0].trim())
				.destinationAccount(parts[1].trim())
				.amount(Double.parseDouble(amountString))
				.date(DateUtils.getDate(parts[3].trim()))
				.transferId(parts[4].trim())
				.build();

			transfers.add(transfer);
		}
	}

	/**
	 * Calculate the final balance for each bank account
	 * @return an unmodifiable map containing account and balance.
	 */
	public Map<String, Double> calculateAccountBalance() {

		for (final Transfer transfer: transfers) {
			final String sourceAccount = transfer.getSourceAccount();
			final String destinationAccount = transfer.getDestinationAccount();
			final double amount = transfer.getAmount();

			if (ZERO_BALANCE.equalsIgnoreCase(sourceAccount)) {
				doOperation(destinationAccount, ADD, amount);
			} else {
				doOperation(sourceAccount, SUBTRACT, amount);
				doOperation(destinationAccount, ADD, amount);
			}
		}
		return Collections.unmodifiableMap(accountBalanceMap);
	}

	/**
	 * @return Account with the highest Balance. This is the first entry in map with maximum value. 
	 * i.e. If Account1 has value 100 and Account2 has value 100, then Account1 will be returned.
	 */
	public String getAccountWithHighestBalance() {
		
		return accountBalanceMap.entrySet().stream()
			.max(Map.Entry.comparingByValue())
			.map(Map.Entry::getKey)
			.orElse(null);
	}

	/**
	 * @return The Frequently used source bank account.
	 * i.e. The most occurring bank account used as sourceAccount in the transfers record.
	 */
	public String getFrequentlyUsedSourceAccount() {

		if (transfers.isEmpty()) { return null; }

		final Map<String, Integer> sourceAccountCount = new HashMap<>();

		for (final Transfer transfer: transfers) {
			final String sourceAccount = transfer.getSourceAccount();

			sourceAccountCount.put(sourceAccount, !ZERO_BALANCE.equalsIgnoreCase(sourceAccount) && sourceAccountCount.containsKey(sourceAccount) ?
				sourceAccountCount.get(sourceAccount) + 1 : 1);
		}

		return Collections.max(sourceAccountCount.entrySet(), Map.Entry.comparingByValue()).getKey();
	}

	private void doOperation(final String account, final String operation, final double amount) {

		if (SUBTRACT.equalsIgnoreCase(operation) && accountBalanceMap.containsKey(account)) {
			accountBalanceMap.put(account, accountBalanceMap.get(account) - amount);
		} else {
			accountBalanceMap.put(account, accountBalanceMap.containsKey(account) ? accountBalanceMap.get(account) + amount : amount);
		}
	}
	
	
}

