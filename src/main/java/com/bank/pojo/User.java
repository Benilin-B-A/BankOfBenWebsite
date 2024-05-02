package com.bank.pojo;

import com.bank.enums.Status;
import com.bank.enums.UserLevel;

public abstract class User {

	protected long userId;
	protected String name;
	protected String dOB;
	protected long phone;
	protected String eMail;
	protected String gender;
	protected Status userStatus;
	protected String address;
	protected UserLevel userLevel;
	protected long createdOn;
	protected long modifiedOn;
	protected long modifiedBy;
	protected long aadharNumber;

	public long getAadharNum() {
		return aadharNumber;
	}

	public void setAadharNum(long aadharNum) {
		this.aadharNumber = aadharNum;
	}

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

	public String toString() {
		return ("Name : " + this.name + "\nDOB : " + this.dOB + "\nGender : " + this.gender + "\nAddress : "
				+ this.eMail + "\nPhone : " + this.phone + "\nStatus : " + this.userStatus + "\nAddress : ");
	}

	public void setLevel(UserLevel level) {
		userLevel = level;
	}

	public UserLevel getLevel() {
		return this.userLevel;
	}

	public long getID() {
		return userId;
	}

	public void setID(long iD) {
		this.userId = iD;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDOB() {
		return dOB;
	}

	public void setDOB(String dOB) {
		this.dOB = dOB;
	}

	public long getPhone() {
		return phone;
	}

	public void setPhone(long phoneString) {
		this.phone = phoneString;
	}

	public String getMail() {
		return eMail;
	}

	public void setMail(String eMailString) {
		this.eMail = eMailString;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public Status getStatus() {
		return userStatus;
	}

	public void setStatus(Status status) {
		this.userStatus = status;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
}
