package com.bank.db.queries;

public class UserTableQuery {
	
	public static String getPassword = "select PASSWORD from User where ID = ?";
	
	public static String isUserPresent = "select exists(select * from User where ID = ?)";

	public static String getAttempts = "select FAILED_ATTEMPTS from Login_Attempts where USER_ID = ?";

	public static String setAttempts = "update Login_Attempts set FAILED_ATTEMPTS = ? where USER_ID = ?";

	public static String getStatus = "select USER_STATE from User where ID = ?";

	public static String setStatus = "update User set USER_STATE = ? where ID = ?";

	public static String getUserLevel = "select LEVEL from User where ID = ?";
	
	public static String changePassword = "update User set PASSWORD = ? where ID = ?";

	public static String addUser = "insert into User(NAME,DOB,PHONE,EMAIL,GENDER,LEVEL,ADDRESS,PASSWORD, CREATED_ON, MODIFIED_BY,AADHAR_NUMBER) "
			+ "values(?,?,?,?,?,?,?,?,?,?,?)";

	public static String getLastPasswordChangeTime = "select LAST_PASSWORD_CHANGE_ON from User where ID = ?";

	public static String updateLastPasswordChangeTime = "update User set LAST_PASSWORD_CHANGE_ON = ? where ID = ?";

	public static String isAlreadyUser = "select exists(select * from User where AADHAR_NUMBER = ?)";
}