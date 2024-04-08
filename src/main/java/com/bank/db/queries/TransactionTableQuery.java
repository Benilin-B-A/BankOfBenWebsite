package com.bank.db.queries;

public class TransactionTableQuery {

	public static String transfer = "insert into Transactions(CUSTOMER_ID,TRANSACTION_ID,ACCOUNT_NUMBER,AMOUNT,TYPE,TRANSACTION_ACC_NUMBER,"
			+ "TIME,OPENING_BAL,CLOSING_BAL,DESCRIPTION) values(?,?,?,?,?,?,?,?,?,?)";;

	public static String getAccountStatement = "select * from Transactions where ACCOUNT_NUMBER = ? "
			+ "order by TRANSACTION_ID desc limit ? offset ?";

	public static String getTransStatement = "select * from Transactions where TRANSACTION_ID = ?";
	
	public static String getTransBranch = "select BRANCH_ID from Accounts inner join Transactions on "
			+ "Transactions.ACCOUNT_NUMBER = Accounts.ACC_NUMBER where TRANSACTION_ID = ?";

	public static String getNoOfPages = "select count(*) from Transactions where ACCOUNT_NUMBER = ?";
}
