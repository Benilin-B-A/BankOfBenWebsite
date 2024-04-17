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
	private String branch;
	protected long createdOn;
	protected long modifiedOn;
	protected long modifiedBy;
	
	public long getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(long createdOn) {
		this.createdOn = createdOn;
	}
	public long getModifiedOn() {
		return modifiedOn;
	}
	public void setModifiedOn(long modifiedOn) {
		this.modifiedOn = modifiedOn;
	}
	public long getModifiedBy() {
		return modifiedBy;
	}
	public void setModifiedBy(long modifiedBy) {
		this.modifiedBy = modifiedBy;
	}
	public String getBranch() {
		return this.branch;
	}
	public void setBranch(String branch) {
		this.branch = branch;
	}

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

	public AccountType getType() {
		return accounType;
	}

	public void setType(AccountType type) {
		this.accounType = type;
	}

	public Status getStatus() {
		return status;
	}

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
