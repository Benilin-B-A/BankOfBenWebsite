package com.bank.pojo;

public class Employee extends User {

	private long branchId;

	public long getBranchID() {
		return branchId;
	}

	public void setBranchID(long branchID) {
		this.branchId = branchID;
	}

	public String toString() {
		return (super.toString() + "\nBranch ID : "	+ this.branchId);
	}
}
