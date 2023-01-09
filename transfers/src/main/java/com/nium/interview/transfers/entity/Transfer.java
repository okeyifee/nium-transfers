package com.nium.interview.transfers.entity;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Data;

/**
 * A transfer object
 * @author okezie okechukwu
 */
@Data
@Builder
public class Transfer {

	private String sourceAccount;
	private String destinationAccount;
	private double amount;
	private LocalDate date;
	private String transferId;
