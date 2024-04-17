package com.bank.db.queries;

public class BranchTableQuery {

	public static String addBranch = "insert into Branch(NAME,IFSC_CODE,ADDRESS, CREATED_ON, MODIFIED_BY) values(?,?,?,?,?)";
	
	public static String isIFSCPresent = "select exists(select * from Branch where IFSC_CODE = ?)";

	public static String isBranchPresent = "select exists(select * from Branch where ID = ?)";

	public static String getAllBranches = "select * from Branch";

}
