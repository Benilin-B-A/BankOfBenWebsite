package com.bank.pojo;

import com.bank.enums.AccountType;
import com.bank.enums.Status;
import com.bank.util.TimeUtil;

public class Account {

	private long userId;
	private long accountNumber;
	private long balance;
	private AccountType accounType;
	private Status status;
	private long branchId;
	private long openedOn;
	private boolean primary;

	public long getUId() {
		return userId;
	}

	public void setUId(long uId) {
		this.userId = uId;
	}

	public boolean isPrimary() {
		return primary;
	}

	public void setPrimary(boolean primary) {
		this.primary = primary;
	}

	public long getAccNum() {
		return accountNumber;
	}

	public void setAccNum(long accNum) {
		this.accountNumber = accNum;
	}

	public long getBalance() {
		return balance;
	}

	public void setBalance(long balance) {
		this.balance = balance;
	}

	@SuppressWarnings("exports")
	public AccountType getType() {
		return accounType;
	}

	@SuppressWarnings("exports")
	public void setType(AccountType type) {
		this.accounType = type;
	}

	@SuppressWarnings("exports")
	public Status getStatus() {
		return status;
	}

	@SuppressWarnings("exports")
	public void setStatus(Status status) {
		this.status = status;
	}

	public long getBranchId() {
		return branchId;
	}

	public void setBranchId(long branchId) {
		this.branchId = branchId;
	}

	public long getOpenedOn() {
		return openedOn;
	}

	public void setOpenedOn(long timestamp) {
		this.openedOn = timestamp;
	}

	public String toString() {
		return ("Customer ID : " + this.userId + " | Account Number : " + this.accountNumber + " | Balance : " + this.balance
				+ " | Branch ID : " + this.branchId + " | Opened On : " + TimeUtil.getDateTime(this.openedOn)
				+ " | Status : " + this.status + " | Type : " + this.accounType);
	}
}
