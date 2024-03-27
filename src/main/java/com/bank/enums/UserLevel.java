package com.bank.enums;

public enum UserLevel {
	
	Customer(1),
	Employee(2),
	Admin(3);
	
	private int userLevel;
	
	private UserLevel(int userLevel) {
		this.userLevel = userLevel;
	}
	
	public int getLevel() {
		return this.userLevel;
	}
}
