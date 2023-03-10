package com.nium.interview.transfers;

import static java.lang.System.lineSeparator;
import static java.lang.System.out;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.nium.interview.transfers.service.TransferService;

import lombok.extern.log4j.Log4j2;

/**
 * <p>
 * 	A Command-line application to parse and process a transfers file and provide the business requirements, namely:
 * 	<ul>
 * 	    <li>1. Print the final balances on all bank accounts</li>
 * 	    <li>2. Print the bank account with the highest balance</li>
 * 	    <li>3. Print the most frequently used source bank account</li>
 * 	</ul>
 * </p>
 */
@SpringBootApplication
@Log4j2
public class TransfersApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(TransfersApplication.class, args);
	}

	@Override
	public void run(final String... args) throws URISyntaxException, IOException {

		if (args.length != 1) {
			log.error("Please provide the name of the file as a command line argument.");
			return;
		}

		final String fileName = args[0];
		
		final URL file = getClass().getClassLoader().getResource(fileName);
		if (null == file) {
			log.error("Resource with name '{}', not found.",  fileName);
			return;
		}

		final String lineSeparator = lineSeparator();
		final TransferService transferService = new TransferService();
		Files.readAllLines(Path.of(file.toURI())).forEach(transferService::saveTransferRecordInList);

		out.println(lineSeparator + "#Balances");
		transferService.calculateAccountBalance().forEach((key, value) -> out.printf("%s - %s%n", key, String.format("%.2f", value)));
		out.println(
			lineSeparator + "#Bank Account with highest balance" + lineSeparator + transferService.getAccountWithHighestBalance());
		out.println(
			lineSeparator + "#Frequently used source bank account" + lineSeparator + transferService.getFrequentlyUsedSourceAccount());

	}
}
