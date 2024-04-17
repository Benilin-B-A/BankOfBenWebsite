package com.bank.db.queries;

public class LogsTableQuery {

	public static String logEvent = "insert into Logs (EVENT, USER_ID, TARGET_ID, DESCRIPTION, TIME)"
			+ " values (?, ?, ?, ?, ?)";
}
