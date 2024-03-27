package com.bank.enums;

public enum Status {
	ACTIVE(1),
	INACTIVE(2),
	BLOCKED(3);
	
private int state;
	
	Status(int state){
		this.state = state;
	}
	
	public int getState() {
		return this.state;
	}
	
	public static Status getStatusByState(int state) {
        for (Status status : Status.values()) {
            if (status.getState() == state) {
                return status;
            }
        }
       return null;
    }
}
