package com.bank.enums;

public enum AccountType {
	CURRENT(1),
	SAVINGS(2);
	
	private int accType;
	
	private AccountType(int accType) {
		this.accType = accType;
	}
	
	public int getType() {
		return this.accType;
	}
	
	public static AccountType getTypeByValue(int value) {
        for (AccountType type : AccountType.values()) {
            if (type.getType() == value) {
                return type;
            }
        }
       return null;
    }
}
