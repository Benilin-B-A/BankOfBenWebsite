package com.bank.db.queries;

public class EmployeeTableQuery {
	
	public static String getBranchId = "select BRANCH_ID from Employee where ID = ?";

	public static String addEmployee = "insert into Employee(ID,BRANCH_ID) values(?,?)";
	
	public static String getEmployeeProfile = "select User.ID,NAME,DOB, GENDER, ADDRESS, EMAIL, PHONE, USER_STATE, BRANCH_ID from Employee "
			+ "inner join User on User.ID = Employee.ID where Employee.ID = ?";

	public static String isEmployeePresent = "select exists(select * from Employee where ID = ?)";
}
