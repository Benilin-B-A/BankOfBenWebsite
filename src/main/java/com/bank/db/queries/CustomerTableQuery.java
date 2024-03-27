package com.bank.db.queries;

public class CustomerTableQuery {

	public static String getPin = "select PIN from Customer where ID = ?";

	public static String addCustomer = "insert into Customer(ID,AADHAR_NUMBER,PAN_NUMBER) values (?,?,?)";

	public static String changePin = "update Customer set PIN = ? where ID = ?";

	public static String getAttempts = "select FAILED_ATTEMPTS from Tpin_Attempts where CUSTOMER_ID = ?";

	public static String setAttempts = "update Tpin_Attempts set FAILED_ATTEMPTS = ? where CUSTOMER_ID = ?";

	public static String getCustomerProfile = "select  NAME,DOB, GENDER, ADDRESS, EMAIL, PHONE, USER_STATE, AADHAR_NUMBER, PAN_NUMBER, NO_OF_ACC "
			+ "from Customer inner join User on User.ID = Customer.ID where Customer.ID = ?";

	public static String isCustomerPresent = "select exists (select * from Accounts where CUSTOMER_ID = ?)";
}
