package com.bank.pojo;

public class Branch {
	
	private long branchId;
	private String branchName;
	private String address;
	private String branchIFSC;

	public long getId() {
		return branchId;
	}

	public void setId(long id) {
		this.branchId = id;
	}

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getIFSC() {
		return branchIFSC;
	}

	public void setIFSC(String iFSC) {
		this.branchIFSC = iFSC;
	}
}
