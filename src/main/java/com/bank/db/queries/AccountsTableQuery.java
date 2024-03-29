package com.bank.db.queries;

public class AccountsTableQuery {

	public static String getBalance = "select BALANCE from Accounts where ACC_NUMBER = ?";

	public static String getPrimaryAccount = "select ACC_NUMBER from Accounts where CUSTOMER_ID = ? and IS_PRIMARY = 1";

	public static String isAccountPresent = "select exists(select * from Accounts where ACC_NUMBER = ?)";

	public static String getCustomerId = "select CUSTOMER_ID from Accounts where ACC_NUMBER = ?";

	public static String changePrimaryAccount = "update Accounts set IS_PRIMARY = case when ACC_NUMBER = ? then"
			+ " 1 when ACC_NUMBER = ? then 0 else IS_PRIMARY end;";

	public static String getAccountDetails = "select CUSTOMER_ID,ACC_NUMBER,BALANCE,ACCOUNT_TYPE,NAME,OPENED_ON,ACC_STATUS from Accounts "
			+ "inner join Branch on Accounts.BRANCH_ID = Branch.ID where ACC_NUMBER = ?";

	public static String getCustomerAccounts = "select * from Accounts where CUSTOMER_ID = ? ";

	public static String addAccount = "insert into Accounts(CUSTOMER_ID,ACCOUNT_TYPE,BRANCH_ID,IS_PRIMARY,OPENED_ON) values (?,?,?,?,?)";

	public static String getAccountStatus = "select ACC_STATUS from Accounts where ACC_NUMBER = ?";

	public static String setAccountStatus = "update Accounts set ACC_STATUS = ? where ACC_NUMBER = ?";

	public static String moveToClosedAccounts = "insert into Closed_Accounts(CUSTOMER_ID,ACC_NUMBER,ACCOUNT_TYPE,BRANCH_ID,OPENED_ON ) "
			+ "select CUSTOMER_ID,ACC_NUMBER,ACCOUNT_TYPE,BRANCH_ID,OPENED_ON  from Accounts where ACC_NUMBER = ?";
	
	public static String closeAccount = "delete from Accounts where ACC_NUMBER = ?";

	public static String getBranchId = "select BRANCH_ID from Accounts where ACC_NUMBER = ?";

	public static String getAccounts = "select ACC_NUMBER from Accounts where CUSTOMER_ID = ?";

}
