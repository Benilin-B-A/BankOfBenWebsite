package com.bank.pojo;

public class Branch {
	
	private long branchId;
	private String branchName;
	private String address;
	private String branchIFSC;
	private long createdOn;
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

	private long modifiedOn;
	private long modifiedBy;
	
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
